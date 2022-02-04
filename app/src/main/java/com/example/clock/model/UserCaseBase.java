package com.example.clock.model;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class UserCaseBase implements Serializable  {

    protected long categoryId;

    public long getTestID() {
        return testID;
    }

    public void setTestID(long testID) {
        this.testID = testID;
    }

    protected long testID = 1;

    protected String mThemeID;

    protected long startTime;
    protected long endTime;

    protected int mNotificationID;

    protected long nextGeneralNotificationMillis;

    protected String mDescription;

    protected String mName;

    protected String mCategoryName;

    protected int color;
    protected boolean completed;
    protected boolean expired;

    protected long timeCreated;
    protected long timeChanged;
    protected long timesCompleted;
    protected long timesExpired;
    protected long timesCancelled;


    protected boolean isImportant;

    public UserCaseBase(){
        categoryId = -1;
        mThemeID = "";
        mNotificationID = (int) Instant.now().toEpochMilli() / 1000;

        long currentMillis = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000;
        timeCreated = currentMillis;
        timeChanged = currentMillis;
    }

    protected String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public String getDescription() {
        return mDescription;
    }

    public int getColor() {
        return color;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getEndTime24(){
        Calendar time = GregorianCalendar.getInstance();
        time.setTimeInMillis(endTime);

        SimpleDateFormat myFormatObj = new SimpleDateFormat("HH:mm");

        return myFormatObj.format(time.getTime());
    }

    public String getEndDate(){
        Calendar time = GregorianCalendar.getInstance();
        time.setTimeInMillis(endTime);

        SimpleDateFormat myFormatObj = new SimpleDateFormat("dd.MM.yy");

        return myFormatObj.format(time.getTime());
    }

    public String getEndDateTime24(){
        Calendar time = GregorianCalendar.getInstance();
        time.setTimeInMillis(endTime);

        SimpleDateFormat myFormatObj = new SimpleDateFormat("dd.MM.yy HH:mm");

        return myFormatObj.format(time.getTime());
    }

    public String getName() {
        return mName;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setDescription(String note) {
        this.mDescription = note;
        setTimeChanged(getCurrentTime());
    }

    public void setRange(String rangeStart, String rangeEnd){
        Calendar calendar = GregorianCalendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            calendar.setTime(sdf.parse(rangeStart));
            startTime = calendar.getTimeInMillis();
        } catch (ParseException e){
            Log.e("TASK ALARM TIME SETUP ERROR: ", e.getMessage());
        }
        try {
            calendar.setTime(sdf.parse(rangeEnd));
            endTime = calendar.getTimeInMillis();
        } catch (ParseException e){
            Log.e("TASK ALARM TIME SETUP ERROR: ", e.getMessage());
        }
    }

    public void setColor(int color) {
        this.color = color;
        setTimeChanged(getCurrentTime());
    }

    public void setCompleted(boolean completed) {
        if(completed){
            this.expired = false;
        }
        if(!this.completed && completed){
            timesCompleted ++;
        }
        else if(this.completed && !completed){
            timesCancelled ++;
        }

        this.completed = completed;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        setTimeChanged(getCurrentTime());
    }

    public void setName(String mName) {
        this.mName = mName;
        setTimeChanged(getCurrentTime());
    }

    public void setCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
        setTimeChanged(getCurrentTime());
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        setTimeChanged(getCurrentTime());
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        setTimeChanged(getCurrentTime());
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
        setTimeChanged(getCurrentTime());
    }



    public String getmCategoryName() {
        return mCategoryName;
    }

    public void setmCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeChanged() {
        return timeChanged;
    }

    public void setTimeChanged(long timeChanged) {
        this.timeChanged = timeChanged;
    }

    public long getTimesCompleted() {
        return timesCompleted;
    }

    public void setTimesCompleted(long timesCompleted) {
        this.timesCompleted = timesCompleted;
    }

    public long getTimesExpired() {
        return timesExpired;
    }

    public void setTimesExpired(long timesExpired) {
        this.timesExpired = timesExpired;
    }

    public long getTimesCancelled() {
        return timesCancelled;
    }

    public void setTimesCancelled(long timesCancelled) {
        this.timesCancelled = timesCancelled;
    }

    protected long getCurrentTime(){
        Calendar calendar = new GregorianCalendar();

        return calendar.getTimeInMillis();
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getThemeID() {
        return mThemeID;
    }

    public void setThemeID(String mThemeID) {
        this.mThemeID = mThemeID;
    }

    public long getNextGeneralNotificationMillis() {
        return nextGeneralNotificationMillis;
    }

    public void setNextGeneralNotificationMillis(long nextGeneralNotificationMillis) {
        this.nextGeneralNotificationMillis = nextGeneralNotificationMillis;
    }

    // Returns true if something has changed (obj got Expired or Completed)
    public boolean markIfExpired(){
        if(completed || expired){
            return false;
        }
        if(endTime != -1) {
            LocalDateTime now = LocalDateTime
                    .ofEpochSecond(LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.UTC), 0, ZoneOffset.UTC);
            LocalDateTime activityEndTime = LocalDateTime
                    .ofEpochSecond(endTime / 1000, 0, ZoneOffset.UTC);
            if(now.isAfter(activityEndTime)){
                expired = true;
                return true;
            }
        }
        return false;
    }

    public int getNotificationID() {
        return mNotificationID;
    }

    public void setNotificationID(int mNotificationID) {
        this.mNotificationID = mNotificationID;
    }
}
