package com.example.clock.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Relation;

public class TaskAndTheme {

    @Embedded
    public Task task;

    @Relation(
            parentColumn = "mThemeID",
            entityColumn = "theme_ID"
    )
    public Theme theme;

    public TaskAndTheme(Task task, Theme theme) {
        this.task = task;
        this.theme = theme;
    }

    public TaskAndTheme(TaskAndTheme other) {
        this.task = other.task;
        this.theme = other.theme;
    }
}
