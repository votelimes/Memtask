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
}
