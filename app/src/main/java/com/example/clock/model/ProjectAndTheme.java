package com.example.clock.model;

import androidx.room.Embedded;

public class ProjectAndTheme {
    @Embedded
    public Project project;
    @Embedded
    public Theme theme;
}
