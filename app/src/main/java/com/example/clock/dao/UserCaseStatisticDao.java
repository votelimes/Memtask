package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clock.model.UserCaseStatistic;

import java.util.List;

@Dao
public interface UserCaseStatisticDao {
    @Insert
    long insert(UserCaseStatistic stat);

    @Query("SELECT * FROM UserCaseStatistic WHERE millisRecordDateTime >= :rangeStartMillis AND millisRecordDateTime < :rangeEndMillis")
    public abstract LiveData<List<UserCaseStatistic>> getUserCaseStatistic(long rangeStartMillis, long rangeEndMillis);

    @Query("SELECT repeatMode FROM TASK_TABLE")
    public abstract LiveData<List<Integer>> getTaskStatistic();

    @Query("SELECT timeCreated FROM project_table")
    public abstract LiveData<List<Long>> getProjectStatistic();
}
