package com.example.clock.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.Tuple2;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
    public LiveData<Tuple2<List<Task>,  List<Theme>>> intermediate;
    private List<Task> monthTasks;
    private List<Task> selectedTasks;
    private List<Integer> daysLoad;
    private int test;

    public CalendarViewModel(Application application, LifecycleOwner lifecycleOwner,  Database database, Database silentDatabase){
        loadData(application, database, silentDatabase);
        intermediate =  LiveDataTransformations.ifNotNull(tasksLiveData, themesLiveData);
        selectedTasks = new ArrayList<Task>();
        monthTasks = new ArrayList<>();
        selectedDateStart = GregorianCalendar.getInstance();
        selectedDateStart.set(Calendar.HOUR_OF_DAY, 0);
        selectedDateEnd = (Calendar) selectedDateStart.clone();
        selectedDateEnd.add(Calendar.DAY_OF_YEAR, 1);
        daysLoad = new ArrayList<Integer>(selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    /*protected void loadData(Application application, Database database, Database silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        tasksLiveData = mRepository.getAllTasksLive();
        projectsLiveData = mRepository.getAllProjectsLive();
        categoriesLiveData = mRepository.getAllCategoriesLive();
        themesLiveData = mRepository.getAllThemesLive();
    }*/
    public void sortDayTasks(int sortType){
        if(sortType == 0){
            selectedTasks = selectedTasks
                    .parallelStream()
                    .sorted(Comparator.comparingLong(Task::getNotificationStartMillis))
                    .collect(Collectors.toList());
        }
    }
    public Task getByPos(int pos){
        return selectedTasks.get(pos);
    }
    public int getPoolSize(){
        return selectedTasks.size();
    }
    public String getPoolItemName(int pos){
        return selectedTasks.get(pos).getName();
    }
    public String getPoolItemDescr(int pos){
        return selectedTasks.get(pos).getDescription();
    }
    public String getTimeRange(int pos){
        Calendar startTime = GregorianCalendar.getInstance();
        Calendar endTime = GregorianCalendar.getInstance();

        startTime.setTimeInMillis(selectedTasks.get(pos).getNotificationStartMillis());
        endTime.setTimeInMillis(selectedTasks.get(pos).getEndTime());


        Date startDate = startTime.getTime();
        Date endDate = endTime.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(startDate) + " — " + sdf.format(endDate);
    }
    public void setDate(CalendarDay startDate, CalendarDay endDate){
        selectedDateStart.setTimeInMillis(startDate.getDate().toEpochDay()*millisInDay);

        if(endDate != null){
            Calendar forcedEndDate = GregorianCalendar.getInstance();
            forcedEndDate.setTimeInMillis(endDate.getDate().toEpochDay()*millisInDay);
            selectedDateEnd = forcedEndDate;
        }
        else{
            selectedDateEnd = selectedDateStart;
            selectedDateEnd.add(Calendar.DAY_OF_YEAR, 1);
        }
        init(selectedDateStart, selectedDateEnd);
    }
    public void removeSilently(int pos){
        removeTaskByIDSilently(selectedTasks.get(pos).getTaskId());
        selectedTasks.remove(pos);
    }
    public int getDayLoad(int position){
        return daysLoad.get(position);
    }
    public Calendar getSelectedDateStart(){
        return selectedDateStart;
    }

    //Util
    public void init(@NonNull Calendar selectedDateStart, Calendar selectedDateEnd){
        if(intermediate.getValue() != null) {
            Calendar newEndCalendar = selectedDateEnd;
            if(newEndCalendar == null){
                newEndCalendar = GregorianCalendar.getInstance();
                newEndCalendar.setTimeInMillis(selectedDateStart.getTimeInMillis());
                newEndCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            filterMonthTasks(selectedDateStart);
            filterSelectedTasks(selectedDateStart, newEndCalendar);
            daysLoad = new ArrayList<Integer>(selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH));
            for(int i = 0; i < selectedDateStart.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                daysLoad.add(new Integer(0));
            }
            calcDaysLoad();
            //sort
        }
    }
    private void filterSelectedTasks(Calendar startDate, Calendar endDate){
        if(monthTasks != null) {
            selectedTasks = monthTasks
                            .parallelStream()
                            .filter(item ->
                                    item.getNotificationStartMillis() >= startDate.getTimeInMillis()
                                            &&
                                            item.getNotificationStartMillis() < (endDate.getTimeInMillis() + millisInDay)
                            )
                            .collect(Collectors.toList());
        }
    }
    private void filterMonthTasks(Calendar calendar){
        int monthMaxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        long millisInMonth = millisInDay * (long) monthMaxDays;
        Calendar currentMonth = GregorianCalendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);
        if(intermediate.getValue().first != null) {
            monthTasks = intermediate
                    .getValue()
                    .first
                    .parallelStream()
                    .filter(item -> item.getNotificationStartMillis() >= currentMonth.getTimeInMillis()
                            &&
                                    item.getNotificationStartMillis() < (currentMonth.getTimeInMillis() + millisInMonth)
                            )
                    .collect(Collectors.toList());
        }
    }
    private void calcDaysLoad(){
        if(monthTasks != null) {
            AtomicInteger monday = new AtomicInteger(0);
            AtomicInteger tuesday = new AtomicInteger(0);
            AtomicInteger wednesday = new AtomicInteger(0);
            AtomicInteger thursday = new AtomicInteger(0);
            AtomicInteger friday = new AtomicInteger(0);
            AtomicInteger saturday = new AtomicInteger(0);
            AtomicInteger sunday = new AtomicInteger(0);
            monthTasks.forEach(task -> {
                boolean repeatFlag = false;
                switch (task.getRepeatMode()) {
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
                        if (task.isMonday()) {
                            monday.addAndGet(1);
                        }
                        if (task.isTuesday()) {
                            tuesday.addAndGet(1);
                        }
                        if (task.isWednesday()) {
                            wednesday.addAndGet(1);
                        }
                        if (task.isThursday()) {
                            thursday.addAndGet(1);
                        }
                        if (task.isFriday()) {
                            friday.addAndGet(1);
                        }
                        if (task.isSaturday()) {
                            saturday.addAndGet(1);
                        }
                        if (task.isSunday()) {
                            sunday.addAndGet(1);
                        }
                        repeatFlag = true;
                }
                if (repeatFlag == false) {
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.setTimeInMillis(task.getNotificationStartMillis());
                    int dayIndex = cal.get(Calendar.DAY_OF_MONTH) - 1;
                    daysLoad.set(dayIndex, daysLoad.get(dayIndex) + 2);
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
}
