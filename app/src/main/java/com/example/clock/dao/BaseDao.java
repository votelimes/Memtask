package com.example.clock.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BaseDao<StorageObject> {

    @Insert
    long insert(StorageObject obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWithReplace(StorageObject obj);

    @Query("SELECT last_insert_rowid()")
    long getLastId();

    @Update
    void update(StorageObject obj);

    @Delete
    void delete(StorageObject obj);
}
