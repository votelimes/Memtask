package com.example.clock.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "project_table")
public class Project extends UserCase{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "projectId")
    public long noteId;

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }
}
