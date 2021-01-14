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
    @ColumnInfo(name = "alarm_note_id")
    public long alarm_note_id;
    protected int repeatMode; // 0: once, 1: every day, 2: every week, 3: every month
    protected Calendar calendar;
    protected String note;



    public AlarmNote(int day_of_week, int hour, int minute, int repeatMode){
        this.calendar = Calendar.getInstance();

        this.calendar.set(Calendar.DAY_OF_WEEK, day_of_week);
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);

        this.repeatMode = repeatMode;
    }

    public AlarmNote(int year, int month, int day_of_month, int hour,
                                                int minute, int repeatMode){
        this.calendar = Calendar.getInstance();

        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month);
        this.calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);

        this.repeatMode = repeatMode;
    }

    public AlarmNote(Calendar calendar, int repeatMode){
        this.calendar = calendar;

        this.repeatMode = repeatMode;
    }

    public void setYear(int year){
        this.calendar.set(Calendar.YEAR, year);
    }
    public void setMonth(int month){
        this.calendar.set(Calendar.MONTH, month);
    }
    public void setDayOfWeek(int dayOfWeek){
        this.calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
    }
    public void setHourOfDay(int hourOfDay){
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
    }
    public void setMinute(int minute){
        this.calendar.set(Calendar.MINUTE, minute);
    }
    public void setNote(String note){
        this.note = note;
    }

    public int getRepeatMode(){
        return this.repeatMode;
    }
    public int getYear(){
        return this.calendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        return this.calendar.get(Calendar.MONTH);
    }
    public int getDayOfWeek(){
        return this.calendar.get(Calendar.DAY_OF_WEEK);
    }
    public int getHourOfDay(){
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getMinute(){
        return this.calendar.get(Calendar.MINUTE);
    }
    public long getTimeInMillis(){
        return this.calendar.getTimeInMillis();
    }
    public String getNote(){
        return this.note;
    }
}
