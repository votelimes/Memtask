package com.votelimes.memtask.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.votelimes.memtask.BR;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskData;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.storageutils.Database;
import com.votelimes.memtask.storageutils.SilentDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.extra.LocalDateRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

        long startMillis = selectedDateStart.toEpochSecond(ZoneOffset.UTC) * 1000;
        long endMillis = selectedDateEnd.toEpochSecond(ZoneOffset.UTC) * 1000;

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
            taskThemePack.getValue().stream().forEach(item -> {
                if (item.task.getStartTime() != 0 && item.task.getEndTime() != 0){
                   if((item.task.getRepeatMode() == 0 || item.task.getRepeatMode() == 4) && (rangeOverlaps(startDate.toLocalDate(), endDate.toLocalDate(), item.task) ||
                           item.task.getStartTime() == 0 || item.task.getEndTime() == 0)){
                       if (item.task.getNotificationStartMillis() >= startDate.toEpochSecond(ZoneOffset.UTC) * 1000
                               &&
                               item.task.getNotificationStartMillis() < endDate.toEpochSecond(ZoneOffset.UTC) * 1000) {
                           selectedTasks.add(new TaskObserver(item));
                       }
                   }
                   else if(item.task.getRepeatMode() == 1
                           && (rangeOverlaps(startDate.toLocalDate(), endDate.toLocalDate(), item.task) ||
                           item.task.getStartTime() == 0 || item.task.getEndTime() == 0)){
                       selectedTasks.add(new TaskObserver(item));
                   }
                   else if(item.task.getRepeatMode() == 2
                           && (checkInRange(startDate, endDate, item.task) ||
                           item.task.getStartTime() == 0 || item.task.getEndTime() == 0)){
                       selectedTasks.add(new TaskObserver(item));
                   }
                   else if(item.task.getRepeatMode() == 3
                           && (checkInRange(startDate, endDate, item.task) ||
                           item.task.getStartTime() == 0 || item.task.getEndTime() == 0)){
                       selectedTasks.add(new TaskObserver(item));
                   }
                }
                else if (item.task.getNotificationStartMillis() >= startDate.toEpochSecond(ZoneOffset.UTC) * 1000
                        &&
                        item.task.getNotificationStartMillis() < endDate.toEpochSecond(ZoneOffset.UTC) * 1000
                        && rangeOverlaps(startDate.toLocalDate(), endDate.toLocalDate(), item.task)) {
                    selectedTasks.add(new TaskObserver(item));
                }
                else if(item.task.getStartTime() == 0 || item.task.getEndTime() == 0){
                    if(item.task.getRepeatMode() == 0 || item.task.getRepeatMode() == 4){
                        if(item.task.getConvertedNotifyMillis() >= startDate.toEpochSecond(ZoneOffset.UTC) * 1000
                        && item.task.getConvertedNotifyMillis() < endDate.toEpochSecond(ZoneOffset.UTC) * 1000){
                            selectedTasks.add(new TaskObserver(item));
                        }
                    }
                    else if(item.task.getRepeatMode() == 1){
                        selectedTasks.add(new TaskObserver(item));
                    }
                    else if(item.task.getRepeatMode() == 2 || item.task.getRepeatMode() == 3){
                        for(LocalDate i = selectedDateStart.toLocalDate();
                            i.isBefore(selectedDateEnd.toLocalDate().plusDays(1));
                            i = i.plusDays(1)){
                            if(item.task.isDayOfWeekActive(i.getDayOfWeek())){
                                selectedTasks.add(new TaskObserver(item));
                                break;
                            }
                        }
                    }
                }
            });
        }
    }
    public void calcDaysLoad(){
        if(taskThemePack.getValue() != null) {
            YearMonth month = YearMonth.from(selectedDateStart);
            LocalDate monthStart = selectedDateStart.withDayOfMonth(1).toLocalDate();
            LocalDate monthEnd = month.atEndOfMonth();
            AtomicInteger rm1u = new AtomicInteger();

            taskThemePack.getValue().forEach(item -> {
                if(item.task.getRepeatMode() != 0
                        && item.task.getRepeatMode() != 4
                        && item.task.getStartTime() != 0
                        && item.task.getEndTime() != 0){
                    LocalDate taskStart = LocalDateTime
                            .ofEpochSecond(item.task.getStartTime() / 1000, 0, ZoneOffset.UTC)
                            .toLocalDate();
                    LocalDate taskEnd = LocalDateTime
                            .ofEpochSecond(item.task.getEndTime() / 1000, 0, ZoneOffset.UTC)
                            .toLocalDate();
                    if(taskEnd.isBefore(monthStart) || taskStart.isAfter(monthEnd)){
                        return;
                    }
                    ///////////////////////////////////////////////////////////////////////////////
                    int dayIndex = 0;
                    if(item.task.getRepeatMode() == 1){
                        LocalDate lwDate;
                        if(monthStart.isBefore(taskStart)){
                            lwDate = taskStart;
                        }
                        else{
                            lwDate = monthStart;
                        }

                        for(LocalDate i = lwDate; i.isBefore(taskEnd.plusDays(1))
                                || i.isBefore(monthEnd.plusDays(1));i = i.plusDays(1)){
                            dayIndex = i.getDayOfMonth() - 1;
                            daysLoad.set(dayIndex,daysLoad.get(dayIndex) + 1);
                        }
                    }
                    else if(item.task.getRepeatMode() == 2 || item.task.getRepeatMode() == 3){
                        LocalDate lwDate;
                        if(monthStart.isBefore(taskStart)){
                            lwDate = taskStart;
                        }
                        else{
                            lwDate = monthStart;
                        }

                        for(LocalDate i = lwDate; i.isBefore(taskEnd.plusDays(1))
                                || i.isBefore(monthEnd.plusDays(1));i = i.plusDays(1)){
                            dayIndex = i.getDayOfMonth() - 1;
                            if(item.task.isDayOfWeekActive(i.getDayOfWeek())){
                                daysLoad.set(dayIndex,daysLoad.get(dayIndex) + 1);
                            }
                        }
                    }
                }
                else{
                    int dayIndex = LocalDateTime
                            .ofEpochSecond(item.task.getNotificationStartMillis()/1000,0,ZoneOffset.UTC)
                            .toLocalDate()
                            .getDayOfMonth() - 1;
                    if(dayIndex >= daysLoad.size()){
                        return;
                    }
                    if(item.task.getRepeatMode() == 0 || item.task.getRepeatMode() == 4) {
                        daysLoad.set(dayIndex, daysLoad.get(dayIndex) + 1);
                    }
                    else if(item.task.getRepeatMode() == 1){
                        rm1u.addAndGet(1);
                    }
                    else if(item.task.getRepeatMode() == 2 || item.task.getRepeatMode() == 3){
                        for(LocalDate day = selectedDateStart.toLocalDate().withDayOfMonth(1);
                        day.toEpochDay() < selectedDateStart
                                .toLocalDate()
                                .withDayOfMonth(1)
                                .plusMonths(1)
                                .toEpochDay(); day = day.plusDays(1)){
                            dayIndex = day.getDayOfMonth() - 1;
                            if(item.task.isDayOfWeekActive(day.getDayOfWeek())){
                                daysLoad.set(dayIndex, daysLoad.get(dayIndex) + 1);
                            }
                        }
                    }

                }
            });
            for(int i = 0; i < daysLoad.size(); i++){
                daysLoad.set(i, daysLoad.get(i) + rm1u.get());
            }
        }
    }

    public boolean checkInRange(LocalDateTime dts, LocalDateTime dte, Task task){
        if(!rangeOverlaps(dts.toLocalDate(), dte.toLocalDate(), task)){
            return false;
        }

        for(LocalDate i = dts.toLocalDate(); i.isBefore(dte.toLocalDate());i = i.plusDays(1)){
            if(task.getRepeatMode() == 2 || task.getRepeatMode() == 3) {
                if (rangeOverlaps(i, i.plusDays(1), task)
                        && task.isDayOfWeekActive(i.getDayOfWeek())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean rangeOverlaps(LocalDate os, LocalDate oe, LocalDate is, LocalDate ie){
        if(os.isEqual(oe)){
            if(os.isEqual(is)){
                return true;
            }
        }
        LocalDateRange outer = LocalDateRange.of(os, oe.plusDays(1));
        LocalDateRange inner = LocalDateRange.of(os, oe.plusDays(1));
        return outer.overlaps(inner);
    }

    private boolean rangeOverlaps(LocalDate os, LocalDate oe, Task task){
        LocalDate is = LocalDateTime
                .ofEpochSecond(task.getStartTime()/1000, 0, ZoneOffset.UTC)
                .toLocalDate();
        LocalDate ie = LocalDateTime
                .ofEpochSecond(task.getEndTime()/1000, 0, ZoneOffset.UTC)
                .toLocalDate();

        LocalDateRange outer = LocalDateRange.of(os, oe);
        LocalDateRange inner = LocalDateRange.of(is, ie.plusDays(1));
        boolean tr = outer.overlaps(inner);
        return outer.overlaps(inner);
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

            StringBuilder sb = new StringBuilder(startTime.format(dtf));
            sb = sb.append(" - ");
            sb = sb.append(endTime.format(dtf));

            return sb.toString();
        }

        public boolean hasRange(){
            if(data.task.getStartTime() == 0
                    || data.task.getStartTime() == 1
                    || data.task.getEndTime() == 0
                    || data.task.getEndTime() == 1
                    || data.task.getStartTime() >= data.task.getEndTime()){
                return false;
            }
            else{
                return true;
            }
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
            notifyPropertyChanged(BR.completenessData);

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
        public Pair<Integer, Integer> getCompletenessData(){
            int completeness = 0;
            if(data.task.isCompleted()){
                completeness = 1;
            }
            else if(data.task.isExpired()){
                completeness = 2;
            }
            int color = data.theme.getSecondColor();
            return new Pair<>(completeness, color);
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
