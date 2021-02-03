package com.example.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {

    @Query("SELECT * FROM alarm_table")
    List <Alarm> getAll();

    @Query("SELECT * FROM alarm_table WHERE alarmId = :id")
    Alarm getById(long id);

    //@Query("SELECT * FROM alarm_table ORDER BY created ASC")
    @Query("SELECT * FROM alarm_table")
    LiveData<List<Alarm>> getAlarmsLive();

    @Query("DELETE FROM alarm_table")
    int clear();

    @Insert
    long insert(Alarm mAlarm);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWithReplace(Alarm mAlarm);

    @Query("SELECT last_insert_rowid()")
    long getLastId();

    @Update
    void update(Alarm mAlarm);

    @Query("DELETE FROM alarm_table WHERE alarmId = :id")
    void deleteById(long id);

    @Delete
    void delete(Alarm mAlarm);
}
