package com.example.clock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "project_table",
        indices = {
        @Index(name = "project_name", value = "mName"),
        @Index(name = "project_catID", value = "categoryId"),
        @Index(name = "project_theme", value = "mThemeID")},

        foreignKeys = {
        @ForeignKey(entity = Theme.class, parentColumns = "theme_ID", childColumns = "mThemeID", onDelete = ForeignKey.NO_ACTION)}

)
public class Project extends UserCaseBase {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "projectId", index = true)
    public String projectId;

    public Project(){
        super();
        this.projectId = generateUUID();
        this.categoryId = "";
    }

    public Project(String name, String description, String categoryID){
        super();
        this.projectId = generateUUID();
        this.mName = name;
        this.mDescription = description;
        this.categoryId = categoryID;
    }

    public Project(Project other) {
        super(other);
        this.projectId = other.projectId;
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
