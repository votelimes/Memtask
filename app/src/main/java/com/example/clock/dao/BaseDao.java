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
public abstract class BaseDao<StorageObject> {

    @Insert
    public abstract long insert(StorageObject obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertWithReplace(StorageObject obj);

    @Query("SELECT last_insert_rowid()")
    public abstract long getLastId();

    @Update
    public abstract void update(StorageObject obj);

    @Delete
    public abstract void delete(StorageObject obj);
}
