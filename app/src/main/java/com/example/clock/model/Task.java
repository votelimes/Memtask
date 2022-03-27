package com.example.clock.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.clock.broadcastreceiver.AlarmBroadcastReceiver;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity(tableName = "task_table")
public class Task extends UserCaseBase {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "task_ID")
    private String taskId;


    protected boolean vibrate;

    protected String ringtonePath;
    protected boolean mediaEnabled;

    // 0: Однократно, 1: Каждый день, 2: По будням, 3: Выбрать дни 4: Ежемесячно
    protected int repeatMode;


    protected boolean notificationEnabled;
    protected long mNotificationStartMillis;

    // 1, 2, 3, 4, 5, 6, 7
    protected boolean sunday;
    protected boolean monday;
    protected boolean tuesday;
    protected boolean wednesday;
    protected boolean thursday;
    protected boolean friday;
    protected boolean saturday;

    protected boolean recurring;
    protected boolean enabled;

    protected boolean notificationInProgress;

    protected int duration = 1;
    protected int progress = 0;

    //protected long testID = 1;
    //protected boolean started;

    protected String mParentID;

    public Task(String name, String description, long catID){
        super();
        mParentID = "";
        ringtonePath = "";
        taskId = generateUUID();
        mName = name;
        mDescription = description;
        categoryId = catID;
        timeCreated = GregorianCalendar.getInstance().getTimeInMillis();
        timeChanged = GregorianCalendar.getInstance().getTimeInMillis();
    }

    public Task(){
        super();
        this.taskId = generateUUID();
        ringtonePath = "";
        mParentID = "";
    }

    public Task(Task other) {
        super(other);
        this.taskId = other.taskId;
        this.vibrate = other.vibrate;
        this.ringtonePath = other.ringtonePath;
        this.mediaEnabled = other.mediaEnabled;
        this.repeatMode = other.repeatMode;
        this.notificationEnabled = other.notificationEnabled;
        this.mNotificationStartMillis = other.mNotificationStartMillis;
        this.sunday = other.sunday;
        this.monday = other.monday;
        this.tuesday = other.tuesday;
        this.wednesday = other.wednesday;
        this.thursday = other.thursday;
        this.friday = other.friday;
        this.saturday = other.saturday;
        this.recurring = other.recurring;
        this.enabled = other.enabled;
        this.notificationInProgress = other.notificationInProgress;
        this.mParentID = other.mParentID;
    }

    public void reGenerateUUID(){
        taskId = generateUUID();
    }

    // 1 if notification in the Past
    public int schedule(Context context) {
        if(notificationEnabled){
            cancelAlarm(context);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(taskId);
        intent.putExtra(TaskNotificationManager.ID_KEY, taskId);
        intent.putExtra(TaskNotificationManager.MODE_KEY, TaskNotificationManager.MODE_INLINE);

        PendingIntent alarmPendingIntent = PendingIntent
                .getBroadcast(context, TaskNotificationManager.REQUEST_CODE_BASE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocalDateTime ldt = LocalDateTime.ofEpochSecond((long) mNotificationStartMillis / 1000, 0, ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // if alarm time has already passed, increment day by 1
        if (ldt.isBefore(LocalDateTime.now()) && repeatMode != 0) {
            ldt.plusDays(1);
        }
        else if(ldt.isBefore(now)){
            return 1;
        }

        switch (repeatMode){
            case 0: // Один раз
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        getConvertedNotifyMillis(),
                        alarmPendingIntent
                );
                break;
            case 1: // Каждый день
                final long RUN_DAILY = 24 * 60 * 60 * 1000;
                final long RUN_TEST = 1000 * 60 * 2;
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        getConvertedNotifyMillis(),
                        RUN_TEST,
                        alarmPendingIntent
                );
                break;
            case 2: // По будням

        }

        /*if (repeatMode == 0) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        }
        else {
            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }*/
        this.notificationEnabled = true;
        return 0;
    }
    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(taskId);
        intent.putExtra("taskID", taskId);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.notificationEnabled = false;
    }
    public void onAlarmRingFinish(){
        if(repeatMode == 0){
            notificationEnabled = false;
        }
    }
    public long getNotificationStartMillis(){
        return mNotificationStartMillis;
    }
    public void setNotificationStartMillis(long millis){
        this.mNotificationStartMillis = millis;
    }
    public void setYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.YEAR, year);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setMonth(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.MONTH, month);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setHourOfDay(int hourOfDay){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setMinute(int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.MINUTE, minute);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setTaskId(String id){
        this.taskId = id;
    }
    public void setRepeatMode(int repeatMode){
        switch (repeatMode){
            case 0:
                setDaysOfWeek(false);
                break;
            case 1:
                setDaysOfWeek(true);
                break;
            case 2:
                setDaysOfWeek(false);
                setWeekdays(true);
                break;
        }

        this.repeatMode = repeatMode;
    }

    public void setVibrate(boolean vibrate){
        this.vibrate = vibrate;
    }

    public void setAlarmTime(long timeInMillis){
        this.mNotificationStartMillis = timeInMillis;
    }
    public void setAlarmTime(String time){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime ldt;

        ldt = LocalDateTime.parse(time, dtf);
        mNotificationStartMillis = ldt.toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    public void setActiveDayOfWeek(int dayOfWeek, boolean state){
        switch (dayOfWeek){
            case 1:
                this.monday = state;
                break;
            case 2:
                this.tuesday = state;
                break;
            case 3:
                this.wednesday = state;
                break;
            case 4:
                this.thursday = state;
                break;
            case 5:
                this.friday = state;
                break;
            case 6:
                this.saturday = state;
                break;
            case 7:
                this.sunday = state;
                break;
        }
        if(dayOfWeek == 0){
            this.sunday = state;
            this.monday = state;
            this.tuesday = state;
            this.wednesday = state;
            this.thursday = state;
            this.friday = state;
            this.saturday = state;
        }
    }
    public void setWeekdays(boolean state){
        setMonday(state);
        setTuesday(state);
        setWednesday(state);
        setThursday(state);
        setFriday(state);
    }
    public void setDaysOfWeek(boolean state){
        setMonday(state);
        setTuesday(state);
        setWednesday(state);
        setThursday(state);
        setFriday(state);
        setSaturday(state);
        setSunday(state);
    }
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public int getRepeatMode(){
        return this.repeatMode;
    }
    public boolean isMediaEnabled() {
        return mediaEnabled;
    }
    public void setMediaEnabled(boolean mediaEnabled) {
        this.mediaEnabled = mediaEnabled;
    }
    public String getRingtonePath() {
        return ringtonePath;
    }
    public void setRingtonePath(String path) {
        this.ringtonePath = path;
    }
    public int getYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.MONTH);
    }
    public boolean isDayOfWeekActive(DayOfWeek dayOfWeek){
        switch(dayOfWeek){
            case MONDAY:
                return monday;
            case TUESDAY:
                return tuesday;
            case WEDNESDAY:
                return wednesday;
            case THURSDAY:
                return thursday;
            case FRIDAY:
                return friday;
            case SATURDAY:
                return saturday;
            case SUNDAY:
                return sunday;
            default:
                return false;
        }
    }

    public boolean getActiveDayOfWeek(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return this.sunday;
            case 2:
                return this.monday;
            case 3:
                return this.tuesday;
            case 4:
                return this.wednesday;
            case 5:
                return this.thursday;
            case 6:
                return this.friday;
            case 7:
                return this.saturday;
            default:
                return false;
        }
    }
    public int getHourOfDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getMinute(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.MINUTE);
    }
    public String getTaskId() {
        return taskId;
    }

    public boolean isEnabled(){
        return  this.enabled;
    }
    public boolean isVibrate(){
        return this.vibrate;
    }
    public boolean isRecurring() {
        return this.recurring;
    }
    public boolean isMonday() {
        return this.monday;
    }
    public boolean isTuesday() {
        return this.tuesday;
    }
    public boolean isWednesday() {
        return this.wednesday;
    }
    public boolean isThursday() {
        return this.thursday;
    }
    public boolean isFriday() {
        return this.friday;
    }
    public boolean isSaturday() {
        return this.saturday;
    }
    public boolean isSunday() {
        return this.sunday;
    }

    protected String toDay(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }
        return "NULL";
    }
    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "Mo ";
        }
        if (tuesday) {
            days += "Tu ";
        }
        if (wednesday) {
            days += "We ";
        }
        if (thursday) {
            days += "Th ";
        }
        if (friday) {
            days += "Fr ";
        }
        if (saturday) {
            days += "Sa ";
        }
        if (sunday) {
            days += "Su ";
        }

        return days;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getParentID() {
        return mParentID;
    }

    public void setParentID(String parentID) {
        this.mParentID = parentID;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public static long getLocalMillis(long UTCMillis){
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(UTCMillis / 1000L, 0, ZoneOffset.UTC);
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        return zdt.toEpochSecond() * 1000L;
    }

    public long getConvertedNotifyMillis(){
        return getLocalMillis(mNotificationStartMillis);
    }

    public boolean isNotificationInProgress() {
        return notificationInProgress;
    }

    public void setNotificationInProgress(boolean notificationInProgress) {
        this.notificationInProgress = notificationInProgress;
    }

    // Returns true if something has changed (obj got Expired or Completed)
    public boolean markIfExpired(long deltaMillis){
        LocalDateTime now = LocalDateTime
                .ofEpochSecond(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - (deltaMillis / 1000), 0, ZoneOffset.UTC);
        if(completed || expired){
            return false;
        }

        if(endTime != -1) {
            LocalDateTime activityEndTime = LocalDateTime.ofEpochSecond(endTime / 1000, 0, ZoneOffset.UTC);
            if(now.isAfter(activityEndTime)){
                expired = true;
                return true;
            }
        }
        else if(notificationEnabled){
            LocalDateTime notificationTime = LocalDateTime.ofEpochSecond(mNotificationStartMillis / 1000, 0, ZoneOffset.UTC);
            if(now.isAfter(notificationTime)){
                expired = true;
                return true;
            }
        }
        return false;
    }
    public boolean markIfExpired(){
        LocalDateTime now = LocalDateTime
                .ofEpochSecond(LocalDateTime.now()
                        .toEpochSecond(ZoneOffset.UTC), 0, ZoneOffset.UTC);

        if(completed || expired){
            return false;
        }

        if(endTime != -1) {
            LocalDateTime activityEndTime = LocalDateTime
                    .ofEpochSecond(endTime / 1000, 0, ZoneOffset.UTC);
            if(now.isAfter(activityEndTime)){
                expired = true;
                return true;
            }
        }
        else if(notificationEnabled){
            LocalDateTime notificationTime = LocalDateTime
                    .ofEpochSecond(mNotificationStartMillis / 1000, 0, ZoneOffset.UTC);
            if(now.isAfter(notificationTime)){
                expired = true;
                return true;
            }
        }
        return false;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        this.progress = 0;
    }

    public void addProgress(){
        progress += getStep();
    }

    public int getProgress(){
        return progress;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public int getStep(){
        LocalDateTime start = LocalDateTime.ofEpochSecond(startTime / 1000, 0, ZoneOffset.UTC);
        LocalDateTime end = LocalDateTime.ofEpochSecond(endTime / 1000, 0, ZoneOffset.UTC);

        double step;
        if(duration > ChronoUnit.DAYS.between(start, end)) {
            step = ((double) ((duration - progress) / (ChronoUnit.DAYS.between(start, end))));
        }
        else if(duration < ChronoUnit.DAYS.between(start, end)){
            step = ((double) ((ChronoUnit.DAYS.between(start, end) / (duration - progress))));
        }
        else{
            step = 1;
        }

        return (int) Math.ceil(step);
    }
}
