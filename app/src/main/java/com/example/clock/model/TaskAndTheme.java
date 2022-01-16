package com.example.clock.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskAndTheme {
    @Embedded
    public Task task;
    @Embedded
    public Theme theme;
}
