package com.example.clock.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity()
public class UserCaseStatistic {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String userCaseID;
    private boolean stateCompleted = false;
    private boolean stateExpired = false;

    private long millisRecordDateTime;

    public UserCaseStatistic(){
        id = 0;
        userCaseID = "";
        stateCompleted = false;
        stateExpired = false;
        millisRecordDateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000;
    }
    public UserCaseStatistic(String ucID, boolean completed, boolean expired){
        userCaseID = ucID;
        stateCompleted = completed;
        stateExpired = expired;
        millisRecordDateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserCaseID() {
        return userCaseID;
    }

    public void setUserCaseID(String userCaseID) {
        this.userCaseID = userCaseID;
    }

    public boolean isStateCompleted() {
        return stateCompleted;
    }

    public void setStateCompleted(boolean stateCompleted) {
        this.stateCompleted = stateCompleted;
    }

    public boolean isStateExpired() {
        return stateExpired;
    }

    public void setStateExpired(boolean stateExpired) {
        this.stateExpired = stateExpired;
    }

    public void setMillisRecordDateTime(long millisRecordDateTime) {
        this.millisRecordDateTime = millisRecordDateTime;
    }

    public void setSecondsRecordDateTime(long secondsRecordDateTime) {
        this.millisRecordDateTime = secondsRecordDateTime * 1000;
    }

    public long getMillisRecordDateTime() {
        return millisRecordDateTime;
    }
}
