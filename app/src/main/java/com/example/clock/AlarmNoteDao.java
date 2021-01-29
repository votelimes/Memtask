package com.example.clock;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmNoteDao {

    @Query("SELECT * FROM alarm_note_table")
    List <AlarmNote> getAll();

    @Query("SELECT * FROM alarm_note_table WHERE alarmNoteId = :id")
    AlarmNote getById(long id);

    @Query("DELETE FROM alarm_note_table")
    int clear();

    @Insert
    long insert(AlarmNote mAlarmNote);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWithReplace(AlarmNote mAlarmNote);

    @Query("SELECT last_insert_rowid()")
    long getLastId();

    @Update
    void update(AlarmNote mAlarmNote);

    @Delete
    void delete(AlarmNote mAlarmNote);
}
