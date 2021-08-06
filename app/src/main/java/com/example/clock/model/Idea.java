package com.example.clock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "idea_table")
public class Idea extends UserCaseBase {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ideaId")
    public long ideaId;

    public Idea(){
        this.ideaId = 0;
        this.description = "";
        this.color = 0;
        this.completed = false;
    }

    public Idea(long ideaId, String description, int color, boolean completed) {
        this.ideaId = ideaId;
        this.description = description;
        this.color = color;
        this.completed = completed;
    }

    public long getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(long ideaId) {
        this.ideaId = ideaId;
    }
}
