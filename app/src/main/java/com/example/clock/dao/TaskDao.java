package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.Task;

import java.util.List;

@Dao
public interface TaskDao extends BaseDao<Task> {

    @Query("SELECT * FROM task_table")
    List <Task> getAll();

    @Query("SELECT * FROM task_table WHERE taskId = :id")
    Task getById(long id);

    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAlarmsLive();

    @Query("DELETE FROM task_table")
    int clear();

    @Query("DELETE FROM task_table WHERE taskId = :id")
    void deleteById(long id);
}
