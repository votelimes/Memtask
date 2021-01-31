package com.example.clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "alarmId")
    public long alarmId;
    protected long timeInMillis;
    protected String note;
    protected boolean vibrate;

    public final static long DAY = 86400000;
    public final static long WEEK = 604800000;

    // 0: once, 1: every day, 2: every week, 3: every month
    protected int repeatMode;
    // 1, 2, 3, 4, 5, 6, 7
    protected boolean sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    protected boolean enabled, started, recurring;


    public Alarm(Calendar calendar, int repeatMode, String note){

        timeInMillis = calendar.getTimeInMillis();

        this.repeatMode = repeatMode;
        this.note = note;
        this.vibrate = true;
    }

    public Alarm(Calendar calendar, int repeatMode){

        timeInMillis = calendar.getTimeInMillis();

        this.note = "";
        this.repeatMode = repeatMode;
        this.vibrate = true;
    }

    public Alarm(long alarmId, int repeatMode, long timeInMillis, String note){
        this.alarmId = alarmId;
        this.repeatMode = repeatMode;
        this.timeInMillis = timeInMillis;
        this.note = note;
        this.vibrate = true;
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
    public void setId(long id){
        this.alarmId = id;
    }
    public void setRepeatMode(int repeatMode){
        this.repeatMode = repeatMode;

        if(repeatMode == 1){
            this.setActiveDayOfWeek(0, true);
        }
    }
    public void setVibrate(boolean vibrate){
        this.vibrate = vibrate;
    }
    public void setNote(String note){
        this.note = note;
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

    public int getRepeatMode(){
        return this.repeatMode;
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
    public boolean getVibrate(){
        return this.vibrate;
    }
    public long getTimeInMillis(){
        return this.timeInMillis;
    }
    public String getNote(){
        return this.note;
    }
    public long getId(){
        return this.alarmId;
    }

    public void cancelAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
//        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
//        alarmManager.cancel(alarmPendingIntent);
//        this.started = false;
//
//        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d", hour, minute, alarmId);
//        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
//        Log.i("cancel", toastText);
    }
}
