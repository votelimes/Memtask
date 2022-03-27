package com.example.clock.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class TaskData {

    public TaskData(){

    }
    public TaskData(Task task, Theme theme){
        this.task = task;
        this.theme = theme;
    }

    @Embedded
    public Task task;
    @Embedded
    public Theme theme;

    @ColumnInfo(name = "categoryName", defaultValue = "")
    public String categoryName;
}
