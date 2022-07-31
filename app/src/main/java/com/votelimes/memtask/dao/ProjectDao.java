package com.votelimes.memtask.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.votelimes.memtask.model.Project;
import com.votelimes.memtask.model.ProjectAndTheme;
import com.votelimes.memtask.model.ProjectData;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

@Dao
public abstract class ProjectDao  extends BaseDao<Project> {

    @Query("SELECT * FROM project_table")
    public abstract List<Project> getAll();

    @Query("SELECT * FROM project_table ORDER BY mName ASC")
    public abstract LiveData<List<Project>> getProjectsLiveData();

    @Query("DELETE FROM project_table")
    public abstract int clear();

    @Query("DELETE FROM project_table WHERE projectId = :id")
    public abstract void deleteById(String id);

    @Query("SELECT project_table.*, theme_table.* FROM project_table LEFT JOIN theme_table ON project_table.mThemeID = theme_table.theme_ID WHERE project_table.projectId = :projectID")
    public abstract LiveData<ProjectAndTheme> getProjectThemeLiveData(String projectID);

    @Query("SELECT project_table.*, theme_table.* FROM project_table LEFT JOIN theme_table ON project_table.mThemeID = theme_table.theme_ID WHERE project_table.categoryId = :categoryID")
    public abstract LiveData<List<ProjectAndTheme>> getProjectsWithThemeLiveData(String categoryID);

    @Transaction
    @Query("SELECT project_table.*, theme_table.* FROM project_table LEFT JOIN theme_table ON project_table.mThemeID = theme_table.theme_ID WHERE project_table.categoryId = :categoryID ORDER BY mName ASC")
    public abstract LiveData<List<ProjectData>> getProjectsDataByCat(String categoryID);

    @Transaction
    @Query("SELECT project_table.*, theme_table.* FROM project_table LEFT JOIN theme_table ON project_table.mThemeID = theme_table.theme_ID WHERE project_table.categoryId = :categoryID AND project_table.mName LIKE :nameRegex")
    public abstract LiveData<List<ProjectData>> getProjectsDataByCatByName(String categoryID, String nameRegex);

    @TestOnly
    @Transaction
    @Query("SELECT project_table.*, theme_table.* FROM project_table LEFT JOIN theme_table ON project_table.mThemeID = theme_table.theme_ID")
    public abstract List<ProjectData> getProjectsDataTEST();

    @TestOnly
    @Transaction
    @Query("SELECT project_table.*, theme_table.* FROM project_table LEFT JOIN theme_table ON project_table.mThemeID = theme_table.theme_ID WHERE project_table.categoryId = :categoryID")
    public abstract List<ProjectData> getProjectsDataByCatTEST(String categoryID);


    @TestOnly
    @Transaction
    @Query("SELECT * from project_table")
    public abstract List<ProjectData> getProjectsDataTEST2();

    @Query("DELETE FROM task_table WHERE task_table.mParentID = :parentID")
    public abstract void deleteSubItems(String parentID);

    @Transaction
    public void deleteWithItemsTransaction(String id){
        deleteById(id);
        deleteSubItems(id);
    }
}
