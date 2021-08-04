package com.example.clock.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IdeaDao extends BaseDao<Idea>{

    @Query("SELECT * FROM idea_table")
    List<Idea> getAll();

    @Query("SELECT * FROM idea_table WHERE ideaId = :id")
    Idea getById(long id);

    @Query("SELECT * FROM idea_table")
    LiveData<List<Idea>> getAlarmsLive();

    @Query("DELETE FROM idea_table")
    int clear();

    @Query("DELETE FROM idea_table WHERE ideaId = :id")
    void deleteById(long id);
}
