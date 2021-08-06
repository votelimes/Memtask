package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.Note;

import java.util.List;

@Dao
public interface NoteDao extends BaseDao<Note> {

    @Query("SELECT * FROM note_table")
    List<Note> getAll();

    @Query("SELECT * FROM note_table WHERE noteId = :id")
    Note getById(long id);

    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAlarmsLive();

    @Query("DELETE FROM note_table")
    int clear();

    @Query("DELETE FROM note_table WHERE noteId = :id")
    void deleteById(long id);
}
