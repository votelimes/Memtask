package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.Project;

import java.util.List;

@Dao
public abstract class ProjectDao  extends BaseDao<Project> {

    @Query("SELECT * FROM project_table")
    public abstract List<Project> getAll();

    @Query("SELECT * FROM project_table ORDER BY mName ASC")
    public abstract LiveData<List<Project>> getProjectsLiveData();

    @Query("DELETE FROM project_table")
    public abstract int clear();

    @Query("DELETE FROM project_table WHERE projectId = :id")
    public abstract void deleteById(String id);
}
