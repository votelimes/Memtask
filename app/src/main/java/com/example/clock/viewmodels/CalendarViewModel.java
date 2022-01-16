package com.example.clock.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.clock.BR;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CalendarViewModel extends MemtaskViewModelBase{
    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;

    private final long millisInDay = 24*60*60*1000;

    private Calendar selectedDateStart;
    private Calendar selectedDateEnd;
    //public LiveData<Tuple2<List<Task>,  List<Theme>>> intermediate;
    private List<TaskAndTheme> selectedTasks;
    private List<Integer> daysLoad;
    private LiveData<List<TaskAndTheme>> taskThemePack;
    public Observer mTaskPoolCountHolder;

    // Modes:
    private MutableLiveData<Integer> updatePending;

    public CalendarViewModel(Application application, Database database, Database silentDatabase){
        selectedDateStart = GregorianCalendar.getInstance();
        selectedDateStart.set(Calendar.HOUR_OF_DAY, 0);
        selectedDateEnd = (Calendar) selectedDateStart.clone();
        selectedDateEnd.add(Calendar.DAY_OF_YEAR, 1);

        loadData(application, database, silentDatabase);
        selectedTasks = new ArrayList<TaskAndTheme>();
        daysLoad = new ArrayList<Integer>(selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        updatePending = new MutableLiveData<>();
        updatePending.setValue(new Integer(0));
        mTaskPoolCountHolder = new Observer();
    }

    @Override
    protected void loadData(Application application, Database database, Database silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        tasksLiveData = null;
        projectsLiveData = null;
        categoriesLiveData = null;
        themesLiveData = mRepository.getAllThemesLive();

        Calendar monthStart = GregorianCalendar.getInstance();
        monthStart.setTimeInMillis(selectedDateStart.getTimeInMillis());
        monthStart.set(Calendar.DAY_OF_MONTH, 1);

        Calendar monthEnd = GregorianCalendar.getInstance();
        monthEnd.setTimeInMillis(selectedDateStart.getTimeInMillis());
        monthEnd.set(Calendar.DAY_OF_MONTH, selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH));

        taskThemePack = mRepository.getTasksLiveDataByNotification(
                monthStart.getTimeInMillis(),
                monthEnd.getTimeInMillis());
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
        return taskThemePack.getValue().get(pos).theme;
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
    public String getTimeRange(int pos){
        Calendar startTime = GregorianCalendar.getInstance();
        Calendar endTime = GregorianCalendar.getInstance();

        startTime.setTimeInMillis(selectedTasks.get(pos).task.getNotificationStartMillis());
        endTime.setTimeInMillis(selectedTasks.get(pos).task.getEndTime());


        Date startDate = startTime.getTime();
        Date endDate = endTime.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(startDate) + " — " + sdf.format(endDate);
    }
    public void setDateAndUpdate(CalendarDay startDate, CalendarDay endDate){
        selectedDateStart.setTimeInMillis(startDate.getDate().toEpochDay()*millisInDay);
        if(endDate != null){
            Calendar forcedEndDate = GregorianCalendar.getInstance();
            forcedEndDate.setTimeInMillis(endDate.getDate().toEpochDay()*millisInDay + millisInDay);
            selectedDateEnd = forcedEndDate;
            init(selectedDateStart, forcedEndDate);
        }
        else{
            selectedDateEnd = GregorianCalendar.getInstance();
            selectedDateEnd.setTimeInMillis(selectedDateStart.getTimeInMillis());
            selectedDateEnd.add(Calendar.DAY_OF_YEAR, 1);
            init(selectedDateStart, selectedDateEnd);
        }
    }
    public void setDate(CalendarDay startDate, CalendarDay endDate){
        selectedDateStart.setTimeInMillis(startDate.getDate().toEpochDay()*millisInDay);
        if(endDate != null) {
            selectedDateEnd.setTimeInMillis(endDate.getDate().toEpochDay() * millisInDay);
        }
        else{
            selectedDateEnd.setTimeInMillis(selectedDateStart.getTimeInMillis());
            selectedDateEnd.add(Calendar.DAY_OF_YEAR, 1);
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
    public Calendar getSelectedDateStart(){
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
        Calendar monthStart = GregorianCalendar.getInstance();
        Calendar monthEnd = GregorianCalendar.getInstance();


        monthStart.setTimeInMillis(selectedDateStart.getTimeInMillis());
        monthEnd.setTimeInMillis(selectedDateStart.getTimeInMillis());
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthEnd.set(Calendar.DAY_OF_MONTH, monthStart.getActualMaximum(Calendar.DAY_OF_MONTH));

        taskThemePack = mRepository.getTasksLiveDataByNotification(
                monthStart.getTimeInMillis(), monthEnd.getTimeInMillis());
        return taskThemePack;
    }
    public LiveData<List<TaskAndTheme>> requestMonthTasksPack(){
        return taskThemePack;
    }


    //Util
    public void init(@NonNull Calendar selectedDateStart, Calendar selectedDateEnd){
        if(taskThemePack.getValue() != null) {
            Calendar localEndCalendar;
            if(selectedDateEnd == null){
                localEndCalendar = GregorianCalendar.getInstance();
                localEndCalendar.setTimeInMillis(selectedDateStart.getTimeInMillis());
                localEndCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            else{
                localEndCalendar = GregorianCalendar.getInstance();
                localEndCalendar.setTimeInMillis(selectedDateEnd.getTimeInMillis());
            }
            if(selectedDateStart.getTimeInMillis() == localEndCalendar.getTimeInMillis()){
                localEndCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            filterSelectedTasks(selectedDateStart, localEndCalendar);
            daysLoad = new ArrayList<Integer>(selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH));
            for(int i = 0; i < selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                daysLoad.add(new Integer(0));
            }
            calcDaysLoad();
            //sort
        }
    }
    public void init(){
        if(taskThemePack.getValue() != null) {
            Calendar localEndCalendar;
            if(selectedDateEnd == null){
                localEndCalendar = GregorianCalendar.getInstance();
                localEndCalendar.setTimeInMillis(selectedDateStart.getTimeInMillis());
                localEndCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            else{
                localEndCalendar = GregorianCalendar.getInstance();
                localEndCalendar.setTimeInMillis(selectedDateEnd.getTimeInMillis());
            }
            if(selectedDateStart.getTimeInMillis() == localEndCalendar.getTimeInMillis()){
                localEndCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            filterSelectedTasks(selectedDateStart, localEndCalendar);
            daysLoad = new ArrayList<Integer>(selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH));
            for(int i = 0; i < selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                daysLoad.add(new Integer(0));
            }
            calcDaysLoad();
            //sort
        }
    }
    private void filterSelectedTasks(Calendar startDate, Calendar endDate){
        if(taskThemePack.getValue() != null) {
            selectedTasks = taskThemePack.getValue()
                            .parallelStream()
                            .filter(item ->
                                    item.task.getNotificationStartMillis() >= startDate.getTimeInMillis()
                                            &&
                                            item.task.getNotificationStartMillis() < endDate.getTimeInMillis()
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
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.setTimeInMillis(item.task.getNotificationStartMillis());
                    int dayIndex = cal.get(Calendar.DAY_OF_MONTH) - 1;
                    daysLoad.set(dayIndex, daysLoad.get(dayIndex) + 3);
                }
            });
            Calendar day = GregorianCalendar.getInstance();
            for (int i = 0; i < daysLoad.size(); i++) {
                day.set(Calendar.DAY_OF_MONTH, i + 1);
                if (day.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    daysLoad.set(i, daysLoad.get(i) + monday.get());
                } else if (day.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                    daysLoad.set(i, daysLoad.get(i) + tuesday.get());
                } else if (day.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                    daysLoad.set(i, daysLoad.get(i) + wednesday.get());
                } else if (day.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                    daysLoad.set(i, daysLoad.get(i) + thursday.get());
                } else if (day.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                    daysLoad.set(i, daysLoad.get(i) + friday.get());
                } else if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    daysLoad.set(i, daysLoad.get(i) + saturday.get());
                } else if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    daysLoad.set(i, daysLoad.get(i) + sunday.get());
                }
            }
        }
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
