package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.clock.model.Project;
import com.example.clock.model.Theme;

import java.util.List;

@Dao
public interface ThemeDao {
    @Insert
    long insert(Theme theme);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWithReplace(Theme theme);

    @Query("SELECT last_insert_rowid()")
    long getLastId();

    @Update
    void update(Theme theme);

    @Delete
    void delete(Theme theme);

    @Query("DELETE FROM theme_table where theme_ID = :id")
    void deleteByID(long id);

    @Query("SELECT * FROM theme_table ORDER BY theme_name ASC")
    LiveData<List<Theme>> getThemesLiveData();
}
