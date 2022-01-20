package com.example.clock.viewmodels;


import android.app.Application;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import androidx.lifecycle.LiveData;


import com.example.clock.BR;
import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.ProjectAndTheme;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Theme;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.Tuple2;
import com.example.clock.storageutils.Tuple3;
import com.example.clock.storageutils.Tuple4;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ManageTaskViewModel extends MemtaskViewModelBase {

    public Observer mManagingTaskRepository;

    public LiveData<Tuple2<List<Theme>, List<Category>>> intermediateThemeAndCategory;

    public LiveData<TaskAndTheme> taskLiveData;
    public LiveData<ProjectAndTheme> projectLiveData;


    String mMode;
    String mTaskID;
    String mParentID;
    long mCategoryID;

    public ManageTaskViewModel(Application application, Database database, Database silentDatabase,
                               String mode, String taskID, long categoryID, String parentID){
        mMode = mode;
        mCategoryID = categoryID;
        mParentID = parentID;
        if(taskID != null && taskID.length() != 0) {
            mTaskID = taskID;
        }
        else{
            mTaskID = "";
        }
        loadData(application, database, silentDatabase);
        intermediateThemeAndCategory = LiveDataTransformations.ifNotNull(themesLiveData, categoriesLiveData);
    }
    protected void loadData(Application application, Database database, Database silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        tasksLiveData = null;
        projectsLiveData = null;
        categoriesLiveData = mRepository.getAllCategoriesLive();
        themesLiveData = mRepository.getAllThemesLive();
        taskLiveData = mRepository.getTaskAndTheme(mTaskID);
        projectLiveData = mRepository.getProjectAndTheme(mTaskID);
    }
    public  LiveData<List<Task>> getTasksData(Application application, Database database, Database silentDatabase){
        if(mRepository == null){
            loadData(application, database, silentDatabase);
        }
        return this.tasksLiveData;
    }
    public  LiveData<List<Project>> getProjectsData(Application application, Database database, Database silentDatabase){
        if(mRepository == null){
            loadData(application, database, silentDatabase);
        }
        return this.projectsLiveData;
    }

    public void initTaskEditing(){
        mManagingTaskRepository = new Observer(taskLiveData.getValue().task);
        mManagingTaskRepository.setTheme(taskLiveData.getValue().theme);
    }
    public void initProjectEditing(){
        mManagingTaskRepository = new Observer(projectLiveData.getValue().project);
        mManagingTaskRepository.setTheme(projectLiveData.getValue().theme);
    }
    public void initCreating(){
        if (mMode.equals("TaskCreating")) {
            mManagingTaskRepository = new Observer(new Task("", "", mCategoryID));
            if (mParentID != null && mParentID.length() != 0) {
                mManagingTaskRepository.mManagingTask.setParentID(mParentID);
            }
            if (App.getSettings().getGenerateRandomThemes()) {
                mManagingTaskRepository.setTheme(getRandomBaseTheme());
            } else {
                intermediateThemeAndCategory.getValue().first.forEach(theme -> {
                    if (theme.getName().equals("MainTaskTheme")) {
                        mManagingTaskRepository.setTheme(theme);
                        return;
                    }
                });
            }
        }
        if (mMode.equals("ProjectCreating")) {
            mManagingTaskRepository = new Observer(new Project("", "", mCategoryID));
            if (App.getSettings().getGenerateRandomThemes()) {
                mManagingTaskRepository.setTheme(getRandomBaseTheme());
            } else {
                intermediateThemeAndCategory.getValue().first.forEach(theme -> {
                    if (theme.getName() == "MainProjectTheme") {
                        mManagingTaskRepository.setTheme(theme);
                        return;
                    }
                });
            }
        }
    }

    public void saveChanges(){
        if(mManagingTaskRepository.themeChanged){
            mManagingTaskRepository.mTheme.reGenerateUUID();
            mManagingTaskRepository.mTheme.setBaseTheme(false);
            mRepository.addTheme(mManagingTaskRepository.mTheme);
        }
        if(mManagingTaskRepository.isTaskMode()) {
            mManagingTaskRepository.mManagingTask.setThemeID(mManagingTaskRepository.mTheme.getID());
            this.mRepository.addTask(this.mManagingTaskRepository.mManagingTask);
        }
        else if(mManagingTaskRepository.isProjectMode()){
            mManagingTaskRepository.mManagingProject.setThemeID(mManagingTaskRepository.mTheme.getID());
            this.mRepository.addProject(this.mManagingTaskRepository.mManagingProject);
        }
    }

    private Theme getRandomBaseTheme(){
        List<Integer> baseThemesIndexes = new ArrayList<>(intermediateThemeAndCategory.getValue().first.size());

        for(int i = 0; i < intermediateThemeAndCategory.getValue().first.size(); i++){
            if(intermediateThemeAndCategory.getValue().first.get(i).isBaseTheme()){
                baseThemesIndexes.add(i);
            }
        }
        return intermediateThemeAndCategory.getValue().first.get(baseThemesIndexes.get(ThreadLocalRandom.current().nextInt(0, baseThemesIndexes.size())));
    }

    public class Observer extends BaseObservable{
        private Task mManagingTask;
        private Project mManagingProject;

        private Theme mTheme;
        public boolean themeChanged = false;
        Observer(Task managingTask){
            this.mManagingTask = managingTask;
            this.mManagingProject = null;
        }

        Observer(Project managingProject){
            this.mManagingTask = null;
            this.mManagingProject = managingProject;
        }

        @Bindable
        public String getCategory(){
            if(intermediateThemeAndCategory.getValue().second == null){
                return "Список категорий пуст";
            }
            long catID = 0;
            if(isTaskMode()){
                catID = mManagingTask.getCategoryId();
            }
            else if(isProjectMode()){
                catID = mManagingProject.getCategoryId();
            }
            if(catID == -1){
                return "";
            }
            else{
                for(int i = 0; i < intermediateThemeAndCategory.getValue().second.size(); i++){
                    Category category = intermediateThemeAndCategory.getValue().second.get(i);
                    if(category.getCategoryId() == catID){
                        return category.getName();
                    }
                }
                return "";
            }
        }

        public void setCategory(Category category){
            if(isTaskMode()){
                mManagingTask.setCategoryId(category.getCategoryId());
            }
            else if(isProjectMode()){
                mManagingProject.setCategoryId(category.getCategoryId());
            }
            notifyPropertyChanged(BR.category);
        }

        public void setManagingTask(Task mManagingTask) {
            this.mManagingTask = mManagingTask;
        }

        public void setManagingProject(Project mManagingProject) {
            this.mManagingProject = mManagingProject;
        }

        @Bindable
        public boolean isVibrate(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isVibrate();
        }

        public void setVibrate(boolean vibrate){
            mManagingTask.setVibrate(vibrate);
            notifyPropertyChanged(BR.vibrate);
        }

        public void setRingtonePath(String path){
            mManagingTask.setRingtonePath(path);
            notifyPropertyChanged(BR.ringtoneString);
        }

        @Bindable
        public String getRingtoneString(){
            if(isProjectMode()){
                return "";
            }
            String ringtonePath = mManagingTask.getRingtonePath();
            if(ringtonePath.length() != 0) {
                Uri uri = Uri.fromFile(new File(ringtonePath));
                Ringtone ring = RingtoneManager.getRingtone(App.getInstance(), uri);
                return ring.getTitle(App.getInstance());
            }
            else{
                return "";
            }
        }

        @Bindable
        public boolean getTaskSoundState(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isMediaEnabled();
        }

        public void setTaskSoundState(boolean state){
            mManagingTask.setMediaEnabled(state);
            notifyPropertyChanged(BR.taskSoundState);
        }

        public Theme getTheme() {
            return mTheme;
        }

        public void setTheme(Theme theme) {
            this.mTheme = theme;
        }

        @Bindable
        public String getTaskName() {
            if(mManagingTask != null){
                return this.mManagingTask.getName();
            }
            else if(mManagingProject != null){
                return this.mManagingProject.getName();
            }

            return "WRONG CASE PASS";
        }

        @Bindable
        public String getTaskDescription(){
            if(mManagingTask != null){
                return this.mManagingTask.getDescription();
            }
            else if(mManagingProject != null){
                return this.mManagingProject.getDescription();
            }

            return "WRONG CASE PASS";
        }

        @Bindable
        public String getTaskRepeatModeString(){
            if(mManagingTask != null){

                String[] stringArray = App.getInstance().getResources().getStringArray(R.array.repeat_modes);
                switch (this.mManagingTask.getRepeatMode()){
                    case 0:
                        return stringArray[0];
                    case 1:
                        return stringArray[1];
                    case 2:
                        return stringArray[2];
                    case 3:
                        return stringArray[3];
                }
            }
            return "WRONG CASE PASS";
        }

        public void setTaskName(String name) {
            if(mManagingTask != null){
                this.mManagingTask.setName(name);
            }
            else if(mManagingProject != null){
                this.mManagingProject.setName(name);
            }
            notifyPropertyChanged(BR.taskDescription);
        }

        public void setTaskDescription(String description){
            if(mManagingTask != null){
                this.mManagingTask.setDescription(description);
            }
            else if(mManagingProject != null){
                this.mManagingProject.setDescription(description);
            }
            notifyPropertyChanged(BR.taskDescription);
        }

        public void setRepeatModeString(String repeatMode){
            if(mManagingTask != null){
                String[] stringArray = App.getInstance().getResources().getStringArray(R.array.repeat_modes);
                if(repeatMode.equals(stringArray[0])){
                    this.mManagingTask.setRepeatMode(0);
                }
                else if(repeatMode.equals(stringArray[1])){
                    this.mManagingTask.setRepeatMode(1);
                }
                else if(repeatMode.equals(stringArray[2])){
                    this.mManagingTask.setRepeatMode(2);
                }
                else if(repeatMode.equals(stringArray[3])){
                    this.mManagingTask.setRepeatMode(3);
                }
            }

            notifyPropertyChanged(BR.monday);
            notifyPropertyChanged(BR.tuesday);
            notifyPropertyChanged(BR.wednesday);
            notifyPropertyChanged(BR.thursday);
            notifyPropertyChanged(BR.friday);
            notifyPropertyChanged(BR.saturday);
            notifyPropertyChanged(BR.sunday);
            notifyPropertyChanged(BR.taskRepeatModeString);
            notifyPropertyChanged(BR.repeatState);
        }

        public boolean isTaskMode(){
            return mManagingTask != null;
        }

        public boolean isProjectMode(){
            return mManagingProject != null;
        }

        public String getStartTime(){
            long startTime = 0;
            if(mManagingTask != null){
                startTime = mManagingTask.getStartTime();
            }
            if(mManagingProject != null){
                startTime = mManagingProject.getStartTime();
            }

            Calendar startTimeCal = GregorianCalendar.getInstance();
            startTimeCal.setTimeInMillis(startTime);

            Date startDate = startTimeCal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            return sdf.format(startDate);
        }

        public String getEndTime(){
            long endTime = 0;
            if(mManagingTask != null){
                endTime = mManagingTask.getEndTime();
            }
            if(mManagingProject != null){
                endTime = mManagingProject.getEndTime();
            }

            Calendar endTimeCal = GregorianCalendar.getInstance();
            endTimeCal.setTimeInMillis(endTime);

            Date endDate = endTimeCal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            return sdf.format(endDate);
        }

        @Bindable
        public String getRange(){
            long firstDateLong = 0;
            long endDateLong = 0;

            if(mManagingTask != null){
                firstDateLong = mManagingTask.getStartTime();
                endDateLong = mManagingTask.getEndTime();
            }
            if(mManagingProject != null){
                firstDateLong = mManagingProject.getStartTime();
                endDateLong = mManagingProject.getEndTime();
            }

            if(firstDateLong == 0 && endDateLong == 0){
                return "";
            }

            Date firstDate=new Date(firstDateLong);
            Date endDate=new Date(endDateLong);

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());


            return sdf2.format(firstDate) + " — " + sdf2.format(endDate) ;
        }

        public void setRangeMillis(long start, long end){
            if(mManagingTask != null){
                mManagingTask.setStartTime(start);
                mManagingTask.setEndTime(end);
            }
            if(mManagingProject != null){
                mManagingTask.setStartTime(start);
                mManagingTask.setEndTime(end);
            }
            notifyPropertyChanged(BR.range);
        }

        @Bindable
        public boolean getRepeatState(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask != null && mManagingTask.getRepeatMode() == 3;
        }

        @Bindable
        public boolean getTaskNotificationState(){
            if(isTaskMode() == false){
                return false;
            }
            return mManagingTask.isNotifyEnabled();
        }

        @Bindable
        public String getTaskNotificationString(){
            long notificationSeconds = 0;

            if(mManagingTask != null){
                notificationSeconds = (long) mManagingTask.getNotificationStartMillis() / 1000;
            }
            if(mManagingProject != null){
                return "";
            }

            if(notificationSeconds == 0 || notificationSeconds == -1){
                return "";
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


            return LocalDateTime.ofEpochSecond(notificationSeconds, 0, ZoneOffset.UTC).format(dtf);
        }

        public void setTaskNotificationState(boolean isEnabled){
            mManagingTask.setNotifyEnabled(isEnabled);
            notifyPropertyChanged(BR.taskNotificationState);
        }

        public void setTaskNotificationMillis(long millis){

            LocalDateTime selectedDate = LocalDateTime.ofEpochSecond((long) millis / 1000, 0, ZoneOffset.UTC);
            if(mManagingTask.isNotifyEnabled() && millis > 0){
                if(mManagingTask.getRepeatMode() == 0){
                    mManagingTask.setNotificationStartMillis(millis);
                }
                else if(mManagingTask.getRepeatMode() == 1){
                    if(selectedDate.isAfter(LocalDateTime.now(ZoneOffset.UTC))){
                        selectedDate.plusDays(1);
                        mManagingTask.setNotificationStartMillis(selectedDate.toEpochSecond(ZoneOffset.UTC) * 1000);
                    }
                }
                else if(mManagingTask.getRepeatMode() == 2 || mManagingTask.getRepeatMode() == 3){
                    LocalDateTime nearest = LocalDateTime.ofEpochSecond((long) millis / 1000, 0, ZoneOffset.UTC);
                    if(mManagingTask.isDayOfWeekActive(selectedDate.getDayOfWeek())
                    && selectedDate.toLocalDate().isEqual(LocalDate.now())
                    && selectedDate.isAfter(LocalDateTime.now())){
                        mManagingTask.setNotificationStartMillis(selectedDate.toEpochSecond(ZoneOffset.UTC) * 1000);
                    }
                    else {
                        int exitDecision = 0;
                        while ((!mManagingTask.isDayOfWeekActive(nearest.getDayOfWeek())
                                || selectedDate.toLocalDate().isEqual(nearest.toLocalDate())
                        ) && exitDecision < 7){
                            nearest = nearest.plusDays(1);
                            exitDecision++;
                        }
                        if(exitDecision > 6){
                            notifyPropertyChanged(BR.taskNotificationString);
                            mManagingTask.setNotificationStartMillis(0);
                        }
                        else {
                            nearest.withHour(selectedDate.getHour());
                            nearest.withMinute(selectedDate.getMinute());
                            mManagingTask.setNotificationStartMillis(nearest.toEpochSecond(ZoneOffset.UTC) * 1000);
                        }
                    }
                }
            }
            notifyPropertyChanged(BR.taskNotificationString);
        }

        public void scheduleOrCancel(Context context){
            if(isTaskMode()){
                if(mManagingTask.isNotifyEnabled()){
                    mManagingTask.schedule(context);
                }
                else{
                    mManagingTask.cancelAlarm(context);
                }
            }
        }

        public int getRepeatMode(){
            if(isProjectMode()){
                return -1;
            }
            return mManagingTask.getRepeatMode();
        }

        public void setDaysOfWeek(boolean state){
            mManagingTask.setDaysOfWeek(state);
        }

        @Bindable
        public int getFirstColor(){
            if(mTheme == null){
                return 1;
            }
            return mTheme.getFirstColor();
        }

        public void setFirstColor(int color){
            themeChanged = true;
            mTheme.setFirstColor(color);
            notifyPropertyChanged(BR.firstColor);
        }

        @Bindable
        public int getSecondColor(){
            if(mTheme == null){
                return 1;
            }
            return mTheme.getSecondColor();
        }

        public void setSecondColor(int color){
            themeChanged = true;
            mTheme.setSecondColor(color);
            notifyPropertyChanged(BR.secondColor);
        }

        @Bindable
        public int getFirstTextColor(){
            if(mTheme == null){
                return 1;
            }
            return mTheme.getMainTextColor();
        }

        public void setFirstTextColor(int color){
            themeChanged = true;
            mTheme.setMainTextColor(color);
            notifyPropertyChanged(BR.firstTextColor);
        }

        @Bindable
        public int getSecondTextColor(){
            if(mTheme == null){
                return 1;
            }
            return mTheme.getAdditionalTextColor();
        }

        public void setSecondTextColor(int color){
            themeChanged = true;
            mTheme.setAdditionalTextColor(color);
            notifyPropertyChanged(BR.secondTextColor);
        }

        @Bindable
        public int getIconColor(){
            if(mTheme == null){
                return 1;
            }
            return mTheme.getIconColor();
        }

        public void setIconColor(int color){
            themeChanged = true;
            mTheme.setIconColor(color);
            notifyPropertyChanged(BR.iconColor);
        }

        @Bindable
        public int getTextColor(){
            if(mTheme == null){
                return 1;
            }
            return mTheme.getMainTextColor();
        }

        public void applyRandomTheme(){
            themeChanged = false;
            mTheme = ManageTaskViewModel.this.getRandomBaseTheme();
            if(isTaskMode() && App.getSettings().getGenerateRandomThemes()){
                mManagingTask.setThemeID(mTheme.getID());
            }
            else if(isProjectMode() && App.getSettings().getGenerateRandomThemes()){
                mManagingProject.setThemeID(mTheme.getID());
            }

            notifyPropertyChanged(BR.firstColor);
            notifyPropertyChanged(BR.secondColor);
            notifyPropertyChanged(BR.firstTextColor);
            notifyPropertyChanged(BR.secondTextColor);
            notifyPropertyChanged(BR.iconColor);
        }

        public boolean isAnyDaySelected(){
            if(isProjectMode()){
                return false;
            }
            if(getMonday() || getTuesday() || getWednesday() || getThursday() || getFriday()
            || getSaturday() || getSunday()){
                return true;
            }
            else{
                return false;
            }
        }

        public void setWeekDays(boolean state){
            mManagingTask.setWeekdays(state);
        }

        @Bindable
        public boolean getMonday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isMonday();
        }
        @Bindable
        public boolean getTuesday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isTuesday();
        }
        @Bindable
        public boolean getWednesday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isWednesday();
        }
        @Bindable
        public boolean getThursday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isThursday();
        }
        @Bindable
        public boolean getFriday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isFriday();
        }
        @Bindable
        public boolean getSaturday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isSaturday();
        }
        @Bindable
        public boolean getSunday(){
            if(isProjectMode()){
                return false;
            }
            return mManagingTask.isSunday();
        }
        // Setters
        public void setMonday(boolean state){
            mManagingTask.setMonday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.monday);
        }
        public void setTuesday(boolean state){
            mManagingTask.setTuesday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.tuesday);
        }
        public void setWednesday(boolean state){
            mManagingTask.setWednesday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.wednesday);
        }
        public void setThursday(boolean state){
            mManagingTask.setThursday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.thursday);
        }
        public void setFriday(boolean state){
            mManagingTask.setFriday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.friday);
        }
        public void setSaturday(boolean state){
            mManagingTask.setSaturday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.saturday);
        }
        public void setSunday(boolean state){
            mManagingTask.setSunday(state);
            setTaskNotificationMillis(mManagingTask.getNotificationStartMillis());
            notifyPropertyChanged(BR.sunday);
        }
    }
}
