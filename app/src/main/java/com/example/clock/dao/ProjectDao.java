package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.Project;

import java.util.List;

@Dao
public interface ProjectDao  extends BaseDao<Project> {

    @Query("SELECT * FROM project_table")
    List<Project> getAll();

    @Query("SELECT * FROM project_table WHERE projectId = :id")
    Project getById(long id);

    @Query("SELECT * FROM project_table")
    LiveData<List<Project>> getAlarmsLive();

    @Query("DELETE FROM project_table")
    int clear();

    @Query("DELETE FROM project_table WHERE projectId = :id")
    void deleteById(long id);
}
