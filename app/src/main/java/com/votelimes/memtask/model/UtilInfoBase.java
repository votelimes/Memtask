package com.example.clock.model;

public class UtilInfoBase {

    private long id;
    private long timeCreated;
    private long timeLastChanged;

    public UtilInfoBase(){
        timeCreated = System.currentTimeMillis();
        timeLastChanged = timeCreated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeLastChanged() {
        return timeLastChanged;
    }

    public void setTimeLastChanged(long timeLastChanged) {
        this.timeLastChanged = timeLastChanged;
    }

}
