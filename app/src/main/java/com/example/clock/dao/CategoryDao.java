package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.clock.model.Category;
import com.example.clock.model.Project;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

@Dao
public abstract class CategoryDao extends BaseDao<Category>{
    @Query("SELECT * FROM category_table")
    public abstract List<Category> getAll();

    @Query("SELECT * FROM category_table ORDER BY categoryName ASC")
    public abstract LiveData<List<Category>> getCategoriesLiveData();

    @TestOnly
    @Query("SELECT * FROM category_table")
    public abstract List<Category> getCategories();

    @Query("DELETE FROM category_table")
    public abstract int clear();

    @Query("DELETE FROM category_table WHERE categoryId = :id")
    public abstract void delete(long id);

    @Query("DELETE FROM task_table WHERE categoryId = :id")
    public abstract void deleteTasksByID(long id);

    @Transaction
    public void deleteWithItemsTransaction(long id){
        delete(id);
        deleteTasksByID(id);
    }
}
