package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseBase;
import com.example.clock.model.UserCaseStatistic;

import java.util.List;

@Dao
public interface UserCaseStatisticDao {
    @Insert
    long insert(UserCaseStatistic stat);

    @Query("SELECT * FROM UserCaseStatistic WHERE millisRecordDateTime >= :rangeStartMillis AND millisRecordDateTime < :rangeEndMillis")
    public abstract LiveData<List<UserCaseStatistic>> getUserCaseStatistic(long rangeStartMillis, long rangeEndMillis);
}
