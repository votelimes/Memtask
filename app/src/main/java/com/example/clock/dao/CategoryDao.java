package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.clock.model.Category;
import com.example.clock.model.Task;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

@Dao
public abstract class CategoryDao extends BaseDao<Category>{
    @Query("SELECT * FROM category_table")
    public abstract List<Category> getAll();

    @Query("SELECT * FROM category_table WHERE (category_table.mOuterID != NULL AND category_table.mOuterID != '')")
    public abstract LiveData<List<Category>> getAllSyncing();

    @Query("SELECT * FROM category_table ORDER BY categoryName ASC")
    public abstract LiveData<List<Category>> getCategoriesLiveData();

    @TestOnly
    @Query("SELECT * FROM category_table")
    public abstract List<Category> getCategories();

    @Query("DELETE FROM category_table")
    public abstract int clear();

    @Query("DELETE FROM category_table WHERE categoryId = :id")
    public abstract void delete(String id);

    @Query("DELETE FROM task_table WHERE categoryId = :id")
    public abstract void deleteTasksByID(String id);

    @Query("DELETE FROM project_table WHERE categoryId = :id")
    public abstract void deleteProjectsByID(String id);

    @Transaction
    public void deleteWithItemsTransaction(String id){
        delete(id);
        deleteTasksByID(id);
        deleteProjectsByID(id);
    }

    public void setGCCatData(List<Category> categories){
        categories.forEach(cat -> {
            insertWithReplace(cat);
        });
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void insertTaskWithReplace(List<Task> list);
}
