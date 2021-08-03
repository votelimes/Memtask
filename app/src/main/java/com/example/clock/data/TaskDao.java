package com.example.clock.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task_table")
    List <Task> getAll();

    @Query("SELECT * FROM task_table WHERE taskId = :id")
    Task getById(long id);

    //@Query("SELECT * FROM alarm_table ORDER BY created ASC")
    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAlarmsLive();

    @Query("DELETE FROM task_table")
    int clear();

    @Insert
    long insert(Task mTask);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWithReplace(Task mTask);

    @Query("SELECT last_insert_rowid()")
    long getLastId();

    @Update
    void update(Task mTask);

    @Query("DELETE FROM task_table WHERE taskId = :id")
    void deleteById(long id);

    @Delete
    void delete(Task mTask);
}
