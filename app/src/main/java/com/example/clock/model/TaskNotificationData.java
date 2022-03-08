package com.example.clock.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskNotificationData {
    @Embedded
    public Task task;

    @Relation(parentColumn = "mParentID", entityColumn = "projectId")
    public Project project;
}
