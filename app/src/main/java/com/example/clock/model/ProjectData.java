package com.example.clock.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ProjectData {
    @Embedded
    public Project project;

    @Relation(
            parentColumn = "mThemeID",
            entityColumn = "theme_ID"
    )
    public Theme theme;

    @Relation(
            entity = Task.class,
            parentColumn = "projectId",
            entityColumn = "mParentID"
    )
    public List<TaskAndTheme> tasksData;
}
