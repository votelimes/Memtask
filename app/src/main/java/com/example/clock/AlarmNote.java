package com.example.clock;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "alarm_note_table")
public class AlarmNote implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "alarmNoteId")
    public long alarmNoteId;
    protected int repeatMode; // 0: once, 1: every day, 2: every week, 3: every month
    protected long timeInMillis;
    protected String note;
    protected boolean vibrate;

    public final static long DAY = 86400000;
    public final static long WEEK = 604800000;

    public AlarmNote(int day_of_week, int hour, int minute, int repeatMode){

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, day_of_week);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        this.timeInMillis = calendar.getTimeInMillis();
        this.repeatMode = repeatMode;
        this.note = "";
        this.vibrate = true;
    }

    public AlarmNote(int year, int month, int day_of_month, int hour,
                                                int minute, int repeatMode){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        this.timeInMillis = calendar.getTimeInMillis();
        this.repeatMode = repeatMode;
        this.note = "";
        this.vibrate = true;
    }

    public AlarmNote(Calendar calendar, int repeatMode, String note){

        timeInMillis = calendar.getTimeInMillis();

        this.repeatMode = repeatMode;
        this.note = note;
        this.vibrate = true;
    }

    public AlarmNote(Calendar calendar, int repeatMode){

        timeInMillis = calendar.getTimeInMillis();

        this.note = "";
        this.repeatMode = repeatMode;
        this.vibrate = true;
    }

    public AlarmNote(long alarmNoteId, int repeatMode, long timeInMillis, String note){
        this.alarmNoteId = alarmNoteId;
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
    public void setDayOfWeek(int dayOfWeek){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

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
        this.alarmNoteId = id;
    }
    public void setRepeatMode(int repeatMode){
        this.repeatMode = repeatMode;
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
        return this.alarmNoteId;
    }
}
