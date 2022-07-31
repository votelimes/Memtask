package com.votelimes.memtask.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TaskNotificationData {
    @Embedded
    public Task task;

    @Relation(parentColumn = "mParentID", entityColumn = "projectId")
    public Project project;
}
