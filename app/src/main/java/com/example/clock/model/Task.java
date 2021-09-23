package com.example.clock.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.clock.broadcastreceiver.AlarmBroadcastReceiver;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity(tableName = "task_table")
public class Task extends UserCaseBase {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "taskId")
    private long taskId;
    protected boolean vibrate;

    // 0: once, 1: every day, 2: every week, 3: every month
    protected int repeatMode;
    protected String repeatModeString;

    // 1, 2, 3, 4, 5, 6, 7
    protected boolean sunday = false, monday = false, tuesday = false,
            wednesday = false, thursday = false, friday = false, saturday = false;

    protected boolean recurring = false;
    protected boolean enabled = false;
    protected boolean started = false;

    public Task(Calendar calendar, int repeatMode, String description){

        timeInMillis = calendar.getTimeInMillis();

        this.repeatMode = repeatMode;
        this.description = description;
        this.vibrate = true;

        switch (repeatMode) {
            case 0: this.repeatModeString = "Once";
                break;
            case 1: this.repeatModeString = "Every day";
                break;
            case 2: this.repeatModeString = "Every week";
                break;
            case 3: this.repeatModeString = "Every month";
                break;
            default: this.repeatModeString = "Once";
        }
    }

    public Task(Calendar calendar, int repeatMode){

        timeInMillis = calendar.getTimeInMillis();

        this.description = "";
        this.repeatMode = repeatMode;
        this.vibrate = true;

        switch (repeatMode) {
            case 0: this.repeatModeString = "Once";
                break;
            case 1: this.repeatModeString = "Every day";
                break;
            case 2: this.repeatModeString = "Every week";
                break;
            case 3: this.repeatModeString = "Every month";
                break;
            default: this.repeatModeString = "Once";
        }
    }

    @Ignore
    public Task(long taskId, int repeatMode, long timeInMillis, String description){
        this.taskId = taskId;
        this.repeatMode = repeatMode;
        this.timeInMillis = timeInMillis;
        this.description = description;
        this.vibrate = true;

        switch (repeatMode) {
            case 0: this.repeatModeString = "Once";
                break;
            case 1: this.repeatModeString = "Every day";
                break;
            case 2: this.repeatModeString = "Every week";
                break;
            case 3: this.repeatModeString = "Every month";
                break;
            default: this.repeatModeString = "Once";
        }
    }

    public Task(long taskId, int repeatMode, boolean started, boolean recurring, long timeInMillis, String description,
                boolean sunday, boolean monday, boolean tuesday, boolean wednesday,
                boolean thursday, boolean friday, boolean saturday){
        this.taskId = taskId;
        this.repeatMode = repeatMode;
        this.started = started;
        this.recurring = recurring;
        this.timeInMillis = timeInMillis;
        this.description = description;
        this.vibrate = true;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;

        switch (repeatMode) {
            case 0: this.repeatModeString = "Once";
                break;
            case 1: this.repeatModeString = "Every day";
                break;
            case 2: this.repeatModeString = "Every week";
                break;
            case 3: this.repeatModeString = "Every month";
                break;
            default: this.repeatModeString = "Once";
        }
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("RECURRING", recurring);
        intent.putExtra("MONDAY", monday);
        intent.putExtra("TUESDAY", tuesday);
        intent.putExtra("WEDNESDAY", wednesday);
        intent.putExtra("THURSDAY", thursday);
        intent.putExtra("FRIDAY", friday);
        intent.putExtra("SATURDAY", saturday);
        intent.putExtra("SUNDAY", sunday);

        intent.putExtra("TITLE", description);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, (int) taskId, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getTimeInMillis());

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!recurring) {
            String toastText = null;
            try {
                toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", description,
                        toDay(calendar.get(Calendar.DAY_OF_WEEK)),
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        }
        else {
            String toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", description, getRecurringDaysText(),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), taskId);

            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }

        this.started = true;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, (int) taskId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), taskId);

        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }

    public void setYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        calendar.set(Calendar.YEAR, year);

        this.timeInMillis = calendar.getTimeInMillis();
    }
    public void setMonth(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        calendar.set(Calendar.MONTH, month);

        this.timeInMillis = calendar.getTimeInMillis();
    }
    public void setHourOfDay(int hourOfDay){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

        this.timeInMillis = calendar.getTimeInMillis();
    }
    public void setMinute(int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        calendar.set(Calendar.MINUTE, minute);

        this.timeInMillis = calendar.getTimeInMillis();
    }
    public void setTaskId(long id){
        this.taskId = id;
    }
    public void setRepeatMode(int repeatMode){
        this.repeatMode = repeatMode;

        if(repeatMode == 1){
            this.setActiveDayOfWeek(0, true);
        }
    }
    public void setRepeatModeString(String repeatMode){
        this.repeatModeString = repeatMode;
    }
    public void setVibrate(boolean vibrate){
        this.vibrate = vibrate;
    }

    public void setTimeInMillis(long timeInMillis){
        this.timeInMillis = timeInMillis;
    }
    public void setDayOfWeek(int dayOfWeek){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        this.timeInMillis = calendar.getTimeInMillis();
    }
    public void setActiveDayOfWeek(int dayOfWeek, boolean state){
        switch (dayOfWeek){
            case 1:
                this.sunday = state;
                break;
            case 2:
                this.monday = state;
                break;
            case 3:
                this.tuesday = state;
                break;
            case 4:
                this.wednesday = state;
                break;
            case 5:
                this.thursday = state;
                break;
            case 6:
                this.friday = state;
                break;
            case 7:
                this.saturday = state;
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
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public int getRepeatMode(){
        return this.repeatMode;
    }
    // 0: once, 1: every day, 2: every week, 3: every month
    public String getRepeatModeString(){
        return this.repeatModeString;
    }
    public int getYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        return calendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        return calendar.get(Calendar.MONTH);
    }
    public int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        return calendar.get(Calendar.DAY_OF_WEEK);
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
        calendar.setTimeInMillis(this.timeInMillis);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getMinute(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        return calendar.get(Calendar.MINUTE);
    }
    public long getTimeInMillis(){
        return this.timeInMillis;
    }
    public long getTaskId(){
        return this.taskId;
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
    public boolean isStarted() {
        return this.started;
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
}
