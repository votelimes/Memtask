package com.example.clock.model;

import java.io.Serializable;

public class UserCaseBase implements Serializable  {

    protected long timeInMillis;
    protected String description;

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

}
