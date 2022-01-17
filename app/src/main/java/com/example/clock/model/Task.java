package com.example.clock.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.clock.broadcastreceiver.AlarmBroadcastReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity(tableName = "task_table")
public class Task extends UserCaseBase {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "task_ID")
    private String taskId;


    protected boolean vibrate;

    // 0: Однократно, 1: Каждый день, 2: По будням, 3: Выбрать дни 4: Ежемесячно
    protected int repeatMode;


    protected boolean notifyEnabled;
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
    //protected boolean started;

    protected String mParentID;

    public Task(String name, String description, long catID){
        super();
        mParentID = "";
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

    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("TASK_ID", taskId);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);

        LocalDateTime ldt = LocalDateTime.ofEpochSecond((long) mNotificationStartMillis / 1000, 0, ZoneOffset.UTC);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mNotificationStartMillis);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        switch (repeatMode){
            case 0: // Один раз
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        ldt.toEpochSecond(ZoneOffset.UTC) * 1000,
                        alarmPendingIntent
                );
            case 1: // Каждый день
                final long RUN_DAILY = 24 * 60 * 60 * 1000;
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        RUN_DAILY,
                        alarmPendingIntent
                );
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

        this.notifyEnabled = true;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("taskID", taskId);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.notifyEnabled = false;
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
        Calendar calendar = GregorianCalendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            calendar.setTime(sdf.parse(time));
            mNotificationStartMillis = calendar.getTimeInMillis();
        } catch (ParseException e){
            Log.e("TASK ALARM TIME SETUP ERROR: ", e.getMessage());
        }

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

    public boolean isNotifyEnabled() {
        return notifyEnabled;
    }

    public void setNotifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
    }
}
