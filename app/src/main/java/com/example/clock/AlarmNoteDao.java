package com.example.clock;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmNoteDao {

    @Query("SELECT * FROM alarm_note_table")
    List <AlarmNote> getAll();

    @Query("SELECT * FROM alarm_note_table WHERE alarmNoteId = :id")
    AlarmNote getById(long id);

    @Insert
    void insert(AlarmNote mAlarmNote);

    @Update
    void update(AlarmNote mAlarmNote);

    @Delete
    void delete(AlarmNote mAlarmNote);
}
