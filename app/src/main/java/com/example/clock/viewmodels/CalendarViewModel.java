package com.example.clock.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.clock.BR;
import com.example.clock.model.Task;
import com.example.clock.model.TaskData;
import com.example.clock.model.Theme;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CalendarViewModel extends MemtaskViewModelBase{
    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;
    public static final int RESTORE_ITEM_SNACKBAR_TIME = 5000;

    private final long millisInDay = 24*60*60*1000;
    private final long secondsInDay = 24*60*60;


    private LocalDateTime selectedDateStart;
    private LocalDateTime selectedDateEnd;
    private List<Integer> daysLoad;
    private LiveData<List<TaskData>> taskThemePack;

    private List<TaskObserver> selectedTasks;

    private TaskObserver removableItemObserver;
    private int removableItemObserverListPos;


    // Modes:
    private MutableLiveData<Integer> updatePending;

    public CalendarViewModel(Application application, Database database, SilentDatabase silentDatabase){
        selectedDateStart = LocalDateTime.now(ZoneOffset.UTC);
        selectedDateStart = selectedDateStart.truncatedTo(ChronoUnit.DAYS);
        selectedDateEnd = selectedDateStart;
        selectedDateEnd = selectedDateEnd.plusDays(1);

        loadData(application, database, silentDatabase);
        selectedTasks = new ArrayList<TaskObserver>();
        daysLoad = new ArrayList<Integer>(selectedDateStart.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
        updatePending = new MutableLiveData<>();
        updatePending.setValue(new Integer(0));
    }

    public int getPoolSize(){
        return selectedTasks.size();
    }
    public int getDayLoad(int position){
        if(daysLoad.size() == 0){
            return 0;
        }
        if(position >= daysLoad.size()){
            return 0;
        }
        return daysLoad.get(position);
    }
    public LocalDateTime getSelectedDateStart(){
        return selectedDateStart;
    }
    public TaskObserver getTaskObserver(int pos){
        return selectedTasks.get(pos);
    }
    public int isUpdatePending() {
        return updatePending.getValue();
    }
    public LiveData<Integer> requestUpdatePendingLD(){
        return updatePending;
    }

    public void setDateAndUpdate(CalendarDay startDate, CalendarDay endDate){
        selectedDateStart = LocalDateTime
                .ofEpochSecond(startDate.getDate()
                        .toEpochDay() * secondsInDay, 0, ZoneOffset.UTC);

        if(endDate != null){
            LocalDateTime forcedEndDate = LocalDateTime
                    .ofEpochSecond(endDate.getDate()
                            .toEpochDay() * secondsInDay + secondsInDay, 0, ZoneOffset.UTC);
            selectedDateEnd = forcedEndDate;
            filterSelectedTasks(selectedDateStart, forcedEndDate);
        }
        else{
            selectedDateEnd = selectedDateStart;
            selectedDateEnd = selectedDateEnd.plusDays(1);
            filterSelectedTasks(selectedDateStart, selectedDateEnd);
        }
    }
    public void setDate(CalendarDay startDate, CalendarDay endDate){

        selectedDateStart = LocalDateTime
                .ofEpochSecond(startDate
                        .getDate().toEpochDay() * secondsInDay, 0, ZoneOffset.UTC);

        if(endDate != null) {
            selectedDateEnd = LocalDateTime
                    .ofEpochSecond(endDate
                            .getDate().toEpochDay() * secondsInDay, 0, ZoneOffset.UTC);
        }
        else{
            selectedDateEnd = selectedDateStart.plusDays(1);
        }
    }
    public void setUpdatePending(int updatePending) {
        this.updatePending.setValue(updatePending);
    }


    //Util
    @Override
    protected void loadData(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(database, silentDatabase);
        tasksLiveData = null;
        projectsLiveData = null;
        categoriesLiveData = null;
        themesLiveData = mRepository.getAllThemesLive();


        long startMillis = 0;
        long endMillis = 0;

        /*if(App.getSettings()
                .getCalendarMode()
                .equals(application.getResources().getStringArray(R.array.preference_calendar_mode_names)[0]))
        {*/
        startMillis = selectedDateStart.toEpochSecond(ZoneOffset.UTC) * 1000;
        endMillis = selectedDateEnd.toEpochSecond(ZoneOffset.UTC) * 1000;

        taskThemePack = mRepository.getTasksByNotification(
                startMillis,
                endMillis);
    }
    public void sortDayTasks(int sortType){
        if(sortType == 0){
            /*selectedTasks = selectedTasks
                    .parallelStream()
                    .sorted(Comparator.comparingLong(Task::getNotificationStartMillis))
                    .collect(Collectors.toList());*/
        }
    }
    public void init(@NonNull LocalDateTime selectedDateStart, LocalDateTime selectedDateEnd){
        if(taskThemePack.getValue() != null) {
            LocalDateTime localEndDateTime;
            if(selectedDateEnd == null){
                localEndDateTime = selectedDateStart.plusDays(1);
            }
            else{
                localEndDateTime = selectedDateEnd;
            }
            if(selectedDateStart.isEqual(localEndDateTime)){
                localEndDateTime = localEndDateTime.plusDays(1);
            }
            filterSelectedTasks(selectedDateStart, localEndDateTime);

            int daysRangeCount = selectedDateStart.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();

            daysLoad = new ArrayList<Integer>(daysRangeCount);

            for(int i = 0; i < daysRangeCount; i++){
                daysLoad.add(new Integer(0));
            }
            calcDaysLoad();
            //sort
        }
    }
    public void init(){
        if(taskThemePack.getValue() != null) {
            LocalDateTime localEndCalendar;
            if(selectedDateEnd == null){
                localEndCalendar = selectedDateStart;
                localEndCalendar = localEndCalendar.plusDays(1);
            }
            else{
                localEndCalendar = selectedDateEnd;
            }
            if(selectedDateStart.isEqual(localEndCalendar)){
                localEndCalendar = localEndCalendar.plusDays(1);
            }

            filterSelectedTasks(selectedDateStart, localEndCalendar);

            int daysRangeCount = selectedDateStart.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();

            daysLoad = new ArrayList<Integer>(daysRangeCount);

            for(int i = 0; i < daysRangeCount; i++){
                daysLoad.add(new Integer(0));
            }
            calcDaysLoad();
            //sort
        }
    }
    public LiveData<List<TaskData>> updateData(String filterName){
        long startMillis = selectedDateStart.toEpochSecond(ZoneOffset.UTC) * 1000;
        long endMillis = selectedDateEnd.toEpochSecond(ZoneOffset.UTC) * 1000;

        if(filterName != null && filterName.length() > 0){
            taskThemePack = mRepository.getTasksByNotificationByName(startMillis, endMillis, filterName);
        }
        else{
            taskThemePack = mRepository.getTasksByNotification(startMillis, endMillis);
        }

        return taskThemePack;
    }
    public LiveData<List<TaskData>> updateMonthTasksPack(){
        LocalDateTime monthStart = selectedDateStart.withDayOfMonth(1);
        LocalDateTime monthEnd = selectedDateStart.with(TemporalAdjusters.lastDayOfMonth());
        monthEnd = monthEnd.plusDays(1);

        taskThemePack = mRepository.getTasksByNotification(
                monthStart.toEpochSecond(ZoneOffset.UTC)*1000, monthEnd.toEpochSecond(ZoneOffset.UTC)*1000);

        return taskThemePack;
    }
    public LiveData<List<TaskData>> requestMonthTasksPack(){
        return taskThemePack;
    }
    public void removeSilently(int pos){

        removableItemObserverListPos = pos;
        removableItemObserver = selectedTasks.get(pos);

        removeTaskByIDSilently(selectedTasks.get(pos).data.task.getTaskId());
        // TODO: remove theme accordingly
        taskThemePack.getValue().remove(selectedTasks.get(pos).getData());
        selectedTasks.remove(pos);
    }

    public int restoreRemovedTask(){
        if(removableItemObserver != null){
            selectedTasks.add(removableItemObserverListPos, removableItemObserver);
            addTaskSilently(removableItemObserver.getTask());
            // TODO: add theme accordingly
        }

        return removableItemObserverListPos;
    }

    private void filterSelectedTasks(LocalDateTime startDate, LocalDateTime endDate){
        if(taskThemePack.getValue() != null) {
            selectedTasks = new ArrayList<>();
            taskThemePack.getValue().parallelStream().forEach(item -> {
                if (item.task.getNotificationStartMillis() >= startDate.toEpochSecond(ZoneOffset.UTC) * 1000
                        &&
                        item.task.getNotificationStartMillis() < endDate.toEpochSecond(ZoneOffset.UTC) * 1000) {
                    selectedTasks.add(new TaskObserver(item));
                }
            });
        }
    }
    public void calcDaysLoad(){
        // Вычислять в зависимости от продолжительности задачи
        if(taskThemePack.getValue() != null) {
            AtomicInteger monday = new AtomicInteger(0);
            AtomicInteger tuesday = new AtomicInteger(0);
            AtomicInteger wednesday = new AtomicInteger(0);
            AtomicInteger thursday = new AtomicInteger(0);
            AtomicInteger friday = new AtomicInteger(0);
            AtomicInteger saturday = new AtomicInteger(0);
            AtomicInteger sunday = new AtomicInteger(0);

            taskThemePack.getValue().forEach(item -> {
                boolean repeatFlag = false;
                switch (item.task.getRepeatMode()) {
                    case 1:
                        monday.addAndGet(1);
                        tuesday.addAndGet(1);
                        wednesday.addAndGet(1);
                        thursday.addAndGet(1);
                        friday.addAndGet(1);
                        saturday.addAndGet(1);
                        sunday.addAndGet(1);
                        repeatFlag = true;
                        break;
                    case 2:
                        monday.addAndGet(1);
                        tuesday.addAndGet(1);
                        wednesday.addAndGet(1);
                        thursday.addAndGet(1);
                        friday.addAndGet(1);
                        repeatFlag = true;
                        break;
                    case 3:
                        if (item.task.isMonday()) {
                            monday.addAndGet(1);
                        }
                        if (item.task.isTuesday()) {
                            tuesday.addAndGet(1);
                        }
                        if (item.task.isWednesday()) {
                            wednesday.addAndGet(1);
                        }
                        if (item.task.isThursday()) {
                            thursday.addAndGet(1);
                        }
                        if (item.task.isFriday()) {
                            friday.addAndGet(1);
                        }
                        if (item.task.isSaturday()) {
                            saturday.addAndGet(1);
                        }
                        if (item.task.isSunday()) {
                            sunday.addAndGet(1);
                        }
                        repeatFlag = true;
                }
                if (repeatFlag == false) {
                    LocalDateTime taskDateTime = LocalDateTime
                            .ofEpochSecond(item.task.getNotificationStartMillis() / 1000, 0, ZoneOffset.UTC);

                    int dayIndex = taskDateTime.getDayOfMonth() - 1;
                    daysLoad.set(dayIndex, daysLoad.get(dayIndex) + 3);
                }
            });
            LocalDateTime current = selectedDateStart;
            //Calendar day = GregorianCalendar.getInstance();

            for (int i = 0; i < daysLoad.size(); i++) {
                current = current.withDayOfMonth(i + 1);
                if (current.getDayOfWeek() == DayOfWeek.MONDAY) {
                    daysLoad.set(i, daysLoad.get(i) + monday.get());
                }
                else if (current.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    daysLoad.set(i, daysLoad.get(i) + tuesday.get());
                }
                else if (current.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    daysLoad.set(i, daysLoad.get(i) + wednesday.get());
                }
                else if (current.getDayOfWeek() == DayOfWeek.THURSDAY) {
                    daysLoad.set(i, daysLoad.get(i) + thursday.get());
                }
                else if (current.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    daysLoad.set(i, daysLoad.get(i) + friday.get());
                }
                else if (current.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    daysLoad.set(i, daysLoad.get(i) + saturday.get());
                }
                else if (current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    daysLoad.set(i, daysLoad.get(i) + sunday.get());
                }
            }
            //Log.d("Completed", "Completed");
        }
    }

    public class TaskObserver extends BaseObservable {
        private TaskData data;

        TaskObserver(TaskData data){
            this.data = data;
        }

        public TaskObserver(TaskObserver other) {
            this.data = other.data;
        }

        @Bindable
        public boolean getCompletedOrExpired(){
            return data.task.isCompleted() || data.task.isExpired();
        }

        public TaskData getData(){
            return data;
        }

        public Task getTask(){
            return this.data.task;
        }

        public Theme getTheme(){
            return this.data.theme;
        }

        public String getRange(){
            LocalDateTime startTime;
            LocalDateTime endTime;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");


            startTime = LocalDateTime.ofEpochSecond(
                    data
                            .task
                            .getStartTime() / 1000,
                    0, ZoneOffset.UTC
            );
            endTime = LocalDateTime.ofEpochSecond(
                    data
                            .task
                            .getEndTime() / 1000,
                    0, ZoneOffset.UTC
            );

            return startTime.format(dtf) + " — " + endTime.format(dtf);
        }

        @Bindable
        public String getCategoryName(){
            return data.categoryName;
        }

        @Bindable
        public boolean getDescriptionState(){
            return data.task.getDescription().length() != 0;
        }

        public boolean isImportant(){
            return data.task.getImportance() == 0;
        }

        public String getNotify(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            return LocalDateTime.ofEpochSecond(
                    (long) data.task.getNotificationStartMillis() / 1000,
                    0, ZoneOffset.UTC).format(dtf);
        }

        @Bindable
        public String getName(){
            return data.task.getName();
        }

        @Bindable
        public String getDescription(){
            return data.task.getDescription();
        }


        public void setName(String name){
            data.task.setName(name);
            CalendarViewModel.this.addTaskSilently(data.task);
            notifyPropertyChanged(BR.name);
        }

        public void setDescription(String description){
            data.task.setDescription(description);
            CalendarViewModel.this.addTaskSilently(data.task);
            notifyPropertyChanged(BR.description);
        }

        public void setCompleted(boolean state){
            if(state){
                data.task.setExpired(false);
                data.task.setCompleted(true);
            }
            else{
                data.task.setExpired(false);
                data.task.setCompleted(false);
            }
            addTaskSilently(data.task);
            notifyPropertyChanged(BR.completedOrExpired);
        }

        @Bindable
        public boolean getNotificationEnabled(){
            return data.task.isNotificationEnabled();
        }

        public int setNotificationEnabled(Context context, boolean state){
            if(data.task.getNotificationStartMillis() == 0){
                return 1;
            }

            if((data.task.getNotificationStartMillis() / 1000) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)){
                return 2;
            }

            data.task.setNotificationEnabled(state);

            int returnCode;

            if(state){
                data.task.schedule(context);
                returnCode = 0;
            }
            else{
                data.task.cancelAlarm(context);
                returnCode = -1;
            }

            addTaskSilently(data.task);
            notifyPropertyChanged(BR.notificationEnabled);

            return returnCode;
        }

        @Bindable
        public Pair<Boolean, Boolean> getCompletedExpired(){
            return new Pair<Boolean, Boolean>(data.task.isCompleted(), data.task.isExpired());
        }

        public void setCompletedExpired(Pair<Boolean, Boolean> data){
            // void
        }

        public String getImage(){
            return data.task.getImageResource();
        }
    }
}
