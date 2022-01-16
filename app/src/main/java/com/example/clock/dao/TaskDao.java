package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.TaskAndTheme;
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

    @Query("DELETE FROM task_table WHERE task_ID = :id")
    public abstract void deleteById(String id);

    @Query("SELECT * FROM task_table WHERE mNotificationStartMillis >= :startMillis AND mNotificationStartMillis < :endMillis ORDER by mNotificationStartMillis ASC")
    public abstract LiveData<List<Task>> getTasksLiveDataByNotification(long startMillis, long endMillis);

    /*@Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID"
            + " WHERE task_table.mNotificationStartMillis >= :startMillis AND task_table.mNotificationStartMillis < :endMillis")*/
    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID"
            + " WHERE task_table.mNotificationStartMillis >= :startMillis AND task_table.mNotificationStartMillis < :endMillis")
    public abstract LiveData<List<TaskAndTheme>> getTasksLiveDataWithTheme(long startMillis, long endMillis);
}
