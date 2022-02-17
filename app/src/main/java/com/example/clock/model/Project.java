package com.example.clock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "project_table")
public class Project extends UserCaseBase {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "projectId")
    public String projectId;

    public Project(){
        super();
        this.projectId = generateUUID();
        this.categoryId = -1;
    }

    public Project(String name, String description, long categoryID){
        super();
        this.projectId = generateUUID();
        this.mName = name;
        this.mDescription = description;
        this.categoryId = categoryID;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void reGenerateUUID(){
        projectId = generateUUID();
    }
}
