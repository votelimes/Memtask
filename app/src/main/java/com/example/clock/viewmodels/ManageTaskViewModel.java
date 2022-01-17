package com.example.clock.viewmodels;


import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import androidx.lifecycle.LiveData;


import com.example.clock.BR;
import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.storageutils.Database;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ManageTaskViewModel extends MemtaskViewModelBase {

    public Observer mManagingTaskRepository;

    public ManageTaskViewModel(Application application, Database database, Database silentDatabase, Task managingTask){
        mManagingTaskRepository = new Observer(managingTask);
        loadData(application, database, silentDatabase);
    }

    public ManageTaskViewModel(Application application, Database database, Database silentDatabase, Project managingProject){
        mManagingTaskRepository = new Observer(managingProject);
        loadData(application, database, silentDatabase);
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

    public void saveChanges(){
        if(mManagingTaskRepository.isTaskMode()) {
            this.mRepository.addTask(this.mManagingTaskRepository.mManagingTask);
        }
        else if(mManagingTaskRepository.isProjectMode()){
            this.mRepository.addProject(this.mManagingTaskRepository.mManagingProject);
        }
    }

    public static class Observer extends BaseObservable{

        private Task mManagingTask;
        private Project mManagingProject;
        public long notificationPickerTimeMillis;
        public long notificationPickerDateMillis;

        Observer(@NonNull Task managingTask){
            this.mManagingTask = managingTask;
            this.mManagingProject = null;
        }

        Observer(@NonNull Project managingProject){
            this.mManagingProject = managingProject;
            this.mManagingTask = null;
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
            return mManagingTask.getRepeatMode();
        }

        public void setDaysOfWeek(boolean state){
            mManagingTask.setDaysOfWeek(state);
        }


        public boolean isAnyDaySelected(){
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
            return mManagingTask.isMonday();
        }
        @Bindable
        public boolean getTuesday(){
            return mManagingTask.isTuesday();
        }
        @Bindable
        public boolean getWednesday(){
            return mManagingTask.isWednesday();
        }
        @Bindable
        public boolean getThursday(){
            return mManagingTask.isThursday();
        }
        @Bindable
        public boolean getFriday(){
            return mManagingTask.isFriday();
        }
        @Bindable
        public boolean getSaturday(){
            return mManagingTask.isSaturday();
        }
        @Bindable
        public boolean getSunday(){
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
