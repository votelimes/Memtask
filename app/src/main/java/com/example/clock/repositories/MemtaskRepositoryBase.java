package com.example.clock.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.clock.dao.CategoryDao;
import com.example.clock.dao.ThemeDao;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Database;
import com.example.clock.dao.ProjectDao;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.dao.TaskDao;

import java.util.List;

public class MemtaskRepositoryBase {

    private Database mDatabase;
    private TaskDao mTaskDao;
    private ProjectDao mProjectDao;
    private CategoryDao mCategoryDao;
    private ThemeDao mThemeDao;

    public MemtaskRepositoryBase(Application application){
        this.mDatabase = Room.databaseBuilder(application, Database.class, "memtask_db")
                .build();

        mTaskDao = mDatabase.taskDao();
        mProjectDao = mDatabase.projectDao();
        mCategoryDao = mDatabase.categoryDao();
        mThemeDao = mDatabase.themeDao();
    }

    //Getting existing data
    public Task getTask (long taskId) {
        return mTaskDao.getById(taskId);
    }

    public LiveData<List<Task>> getAllTasksLive(){
        return this.mDatabase.taskDao().getTasksLiveData();
    }

    public Project getProject (long projectId) {
        return mProjectDao.getById(projectId);
    }

    public LiveData<List<Project>> getAllProjectsLive(){
        return this.mDatabase.projectDao().getProjectsLiveData();
    }

    public Category getCategory (long categoryId) {
        return mCategoryDao.getById(categoryId);
    }

    public LiveData<List<Category>> getAllCategoriesLive(){
        return this.mDatabase.categoryDao().getCategoriesLiveData();
    }

    public LiveData<List<Theme>> getAllThemesLive(){
        return this.mDatabase.themeDao().getThemesLiveData();
    }


    // Adding new data
    public void addTask (Task newTask) {
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.insertWithReplace(newTask);
        });
    }

    public void addProject (Project newProject) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.insertWithReplace(newProject);
        });
    }

    public void addCategory (Category newCategory){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insertWithReplace(newCategory);
        });
    }

    public void addTheme(Theme theme){
        Database.databaseWriteExecutor.execute(() -> {
            mThemeDao.insertWithReplace(theme);
        });
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        mTaskDao.delete(removableTask);
    }

    public void removeProject (Project removableProject) {
        mProjectDao.delete(removableProject);
    }

    public void removeCategory (Category removableCategory) {
        mCategoryDao.delete(removableCategory);
    }

    public void removeThemeByID (long themeID){
        mThemeDao.deleteByID(themeID);
    }


    //Updating existing data
    public void updateTask (Task updatableTask) {
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.update(updatableTask);
        });
    }

    public void updateProject (Project updatableProject) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.update(updatableProject);
        });
    }

    public void updateCategory (Category updatableCategory) {
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.update(updatableCategory);
        });
    }

    public void updateTheme (Theme theme){
        Database.databaseWriteExecutor.execute(() -> {
            mThemeDao.update(theme);
        });
    }

}
