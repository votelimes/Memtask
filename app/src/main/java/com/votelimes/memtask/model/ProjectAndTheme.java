package com.votelimes.memtask.model;

import androidx.room.Embedded;

public class ProjectAndTheme {
    @Embedded
    public Project project;
    @Embedded
    public Theme theme;
}
