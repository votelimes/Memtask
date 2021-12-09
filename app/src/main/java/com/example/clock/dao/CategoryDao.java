package com.example.clock.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.clock.model.Category;
import com.example.clock.model.Project;

import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category>{
    @Query("SELECT * FROM category_table")
    List<Category> getAll();

    @Query("SELECT * FROM category_table WHERE categoryId = :id")
    Category getById(long id);

    @Query("SELECT * FROM category_table ORDER BY mName ASC")
    LiveData<List<Category>> getCategoriesLiveData();

    @Query("DELETE FROM category_table")
    int clear();

    @Query("DELETE FROM category_table WHERE categoryId = :id")
    void deleteById(long id);
}
