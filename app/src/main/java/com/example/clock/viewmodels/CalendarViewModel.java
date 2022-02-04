package com.example.clock.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.clock.BR;
import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CalendarViewModel extends MemtaskViewModelBase{
    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;

    private final long millisInDay = 24*60*60*1000;
    private final long secondsInDay = 24*60*60;

    private LocalDateTime selectedDateStart;
    private LocalDateTime selectedDateEnd;
    //public LiveData<Tuple2<List<Task>,  List<Theme>>> intermediate;
    private List<TaskAndTheme> selectedTasks;
    private List<Integer> daysLoad;
    private LiveData<List<TaskAndTheme>> taskThemePack;
    public Observer mTaskPoolCountHolder;

    // Modes:
    private MutableLiveData<Integer> updatePending;

    public CalendarViewModel(Application application, Database database, SilentDatabase silentDatabase){
        selectedDateStart = LocalDateTime.now(ZoneOffset.UTC);
        selectedDateStart = selectedDateStart.truncatedTo(ChronoUnit.DAYS);
        selectedDateEnd = selectedDateStart;
        selectedDateEnd = selectedDateEnd.plusDays(1);

        loadData(application, database, silentDatabase);
        selectedTasks = new ArrayList<TaskAndTheme>();
        daysLoad = new ArrayList<Integer>(selectedDateStart.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
        updatePending = new MutableLiveData<>();
        updatePending.setValue(new Integer(0));
        mTaskPoolCountHolder = new Observer();
    }

    @Override
    protected void loadData(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
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

        taskThemePack = mRepository.getTasksLiveDataByNotification(
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
    public Task getTaskByPos(int pos){
        return selectedTasks.get(pos).task;
    }
    public Theme getThemeByPos(int pos){
        return selectedTasks.get(pos).theme;
    }
    public String getTaskNotifyString(int pos){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        return LocalDateTime.ofEpochSecond(
                (long) getTaskByPos(pos).getNotificationStartMillis() / 1000,
                0, ZoneOffset.UTC).format(dtf);
    }


    public int getPoolSize(){
        return selectedTasks.size();
    }
    public String getPoolItemName(int pos){
        return selectedTasks.get(pos).task.getName();
    }
    public String getPoolItemDescr(int pos){
        return selectedTasks.get(pos).task.getDescription();
    }
    public String getCategoryName(int pos){
        return selectedTasks.get(pos).categoryName;
    }
    public String getTimeRange(int pos){
        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        startTime = LocalDateTime.ofEpochSecond(selectedTasks
                        .get(pos)
                        .task
                        .getStartTime() / 1000,
                0, ZoneOffset.UTC
                );
        endTime = LocalDateTime.ofEpochSecond(selectedTasks
                        .get(pos)
                        .task
                        .getEndTime() / 1000,
                0, ZoneOffset.UTC
        );

        return startTime.format(dtf) + " — " + endTime.format(dtf);
    }
    public boolean hasDescription(int pos){
        return !selectedTasks.get(pos).task.getDescription().equals("");
    }
    public void setDateAndUpdate(CalendarDay startDate, CalendarDay endDate){

        LocalDateTime selectedDateStart = LocalDateTime
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
    public void removeSilently(int pos){
        removeTaskByIDSilently(selectedTasks.get(pos).task.getTaskId());
        selectedTasks.remove(pos);
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


    public LiveData<Integer> requestUpdatePendingLD(){
        return updatePending;
    }
    public int isUpdatePending() {
        return updatePending.getValue();
    }
    public void setUpdatePending(int updatePending) {
        this.updatePending.setValue(updatePending);
    }

    public LiveData<List<TaskAndTheme>> updateMonthTasksPack(){
        LocalDateTime monthStart = selectedDateStart.withDayOfMonth(1);
        LocalDateTime monthEnd = selectedDateStart.with(TemporalAdjusters.lastDayOfMonth());
        monthEnd = monthEnd.plusDays(1);

        taskThemePack = mRepository.getTasksLiveDataByNotification(
                monthStart.toEpochSecond(ZoneOffset.UTC)*1000, monthEnd.toEpochSecond(ZoneOffset.UTC)*1000);

        return taskThemePack;
    }
    public LiveData<List<TaskAndTheme>> requestMonthTasksPack(){
        return taskThemePack;
    }


    //Util
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

    private void filterSelectedTasks(LocalDateTime startDate, LocalDateTime endDate){
        if(taskThemePack.getValue() != null) {
            selectedTasks = taskThemePack.getValue()
                            .parallelStream()
                            .filter(item ->
                                    item.task.getNotificationStartMillis() >= startDate.toEpochSecond(ZoneOffset.UTC) * 1000
                                            &&
                                            item.task.getNotificationStartMillis() < endDate.toEpochSecond(ZoneOffset.UTC) * 1000
                            )
                            .collect(Collectors.toList());
            mTaskPoolCountHolder.setSelectedDayTasksCount(selectedTasks.size());
        }
        else{
            mTaskPoolCountHolder.setSelectedDayTasksCount(0);
        }
    }
    private void calcDaysLoad(){
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

    public void removeTask(int position){
        removeTaskByID(selectedTasks.get(position).task.getTaskId());
    }

    //
    public static class Observer extends BaseObservable {
        private int selectedDayTasksCount = 0;
        Observer(){

        }

        public void setSelectedDayTasksCount(int value){
            this.selectedDayTasksCount = value;
            notifyPropertyChanged(BR.selectedDayTasksCount);
        }
        @Bindable
        public int getSelectedDayTasksCount(){
            return this.selectedDayTasksCount;
        }
    }      
}
