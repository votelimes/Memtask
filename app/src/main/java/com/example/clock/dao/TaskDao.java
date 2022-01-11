package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.Task;

import java.util.List;

@Dao
public abstract class TaskDao extends BaseDao<Task> {

    @Query("SELECT * FROM task_table")
    public abstract List <Task> getAll();

    //@Query("SELECT * FROM task_table ORDER BY timeInMillis DESC")
    @Query("SELECT * FROM task_table ORDER by mName ASC")
    public abstract LiveData<List<Task>> getTasksLiveData();

    @Query("DELETE FROM task_table")
    public abstract int clear();

    @Query("DELETE FROM task_table WHERE taskId = :id")
    public abstract void deleteById(String id);
}
