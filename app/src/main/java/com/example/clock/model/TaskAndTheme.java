package com.example.clock.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskAndTheme {

    public TaskAndTheme(){

    }
    public TaskAndTheme(Task task, Theme theme){
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
