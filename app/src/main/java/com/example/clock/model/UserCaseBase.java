package com.example.clock.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UserCaseBase implements Serializable  {

    protected long categoryId;

    protected long timeInMillis;

    protected String description;

    protected String mName;

    protected String mCategoryName;

    protected int color;
    protected boolean completed;

    public UserCaseBase(){

        timeInMillis = 0;
        description = "null";
        color = 0;
        completed = false;
    }
    public long getTimeInMillis(){
        return timeInMillis;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getTime24() {
        Calendar time = GregorianCalendar.getInstance();
        time.setTimeInMillis(timeInMillis);

        SimpleDateFormat myFormatObj = new SimpleDateFormat("HH:mm");

        return myFormatObj.format(time.getTime());
    }

    public String getName() {
        return mName;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setDescription(String note) {
        this.description = note;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }
}
