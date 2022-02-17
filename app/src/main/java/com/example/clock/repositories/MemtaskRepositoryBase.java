package com.example.clock.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.clock.dao.CategoryDao;
import com.example.clock.dao.ThemeDao;
import com.example.clock.dao.UserCaseStatisticDao;
import com.example.clock.model.ProjectAndTheme;
import com.example.clock.model.ProjectData;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.TaskData;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.storageutils.Database;
import com.example.clock.dao.ProjectDao;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.dao.TaskDao;
import com.example.clock.storageutils.SilentDatabase;

import java.util.List;

public class MemtaskRepositoryBase {

    private final Database mDatabase;
    private final SilentDatabase mSilentDatabase;
    private final TaskDao mTaskDao;
    private final TaskDao mSilentTaskDao;
    private final ProjectDao mProjectDao;
    private final ProjectDao mSilentProjectDao;
    private final CategoryDao mCategoryDao;
    private final ThemeDao mThemeDao;
    private final UserCaseStatisticDao mUserCaseStatisticDao;

    public MemtaskRepositoryBase(Application application, Database database, SilentDatabase silentDatabase){
        this.mDatabase = database;
        this.mSilentDatabase = silentDatabase;

        mTaskDao = mDatabase.taskDao();
        mProjectDao = mDatabase.projectDao();
        mCategoryDao = mDatabase.categoryDao();
        mThemeDao = mDatabase.themeDao();
        mSilentTaskDao = mSilentDatabase.taskDao();
        mSilentProjectDao = mSilentDatabase.projectDao();
        mUserCaseStatisticDao = mDatabase.userCaseStatisticDao();
    }

    //Getting existing data

    public LiveData<List<Task>> getAllTasksLive(){
        return this.mDatabase.taskDao().getTasksLiveData();
    }

    public LiveData<List<Project>> getAllProjectsLive(){
        return this.mDatabase.projectDao().getProjectsLiveData();
    }

    public LiveData<List<Category>> getAllCategoriesLive(){
        return this.mDatabase.categoryDao().getCategoriesLiveData();
    }

    public LiveData<List<Theme>> getAllThemesLive(){
        return this.mDatabase.themeDao().getThemesLiveData();
    }

    public LiveData<List<TaskData>> getTasksLiveDataByNotification(long startMillis, long endMillis){
        return this.mDatabase.taskDao() .getTasksWithThemeLiveData(startMillis, endMillis);
    }

    public LiveData<TaskData> getTaskAndTheme(String taskID){
        return this.mDatabase.taskDao().getTaskThemeLiveData(taskID);
    }

    public LiveData<ProjectAndTheme> getProjectAndTheme(String projectID){
        return this.mDatabase.projectDao().getProjectThemeLiveData(projectID);
    }

    public LiveData<List<TaskData>> getTaskDataByCategory(long ID){
        return this.mDatabase.taskDao().getTasksWithThemeLiveData(ID);
    }

    public LiveData<List<ProjectData>> getProjectDataByCategory(long ID){
        return this.mDatabase.projectDao().getProjectsDataByCat(ID);
    }

    public LiveData<List<UserCaseStatistic>> getUserCaseStatistic(long rangeStartMillis, long rangeEndMillis){
        return this.mDatabase.userCaseStatisticDao().getUserCaseStatistic(rangeStartMillis, rangeEndMillis);
    }

    public LiveData<List<TaskData>> getSingleTasksByCategoryLiveData(long categoryID){
        return this.mDatabase.taskDao().getSingleTasksWithThemeLiveData(categoryID);
    }

    public LiveData<List<TaskData>> getProjectTasksByCategoryLiveData(long categoryID){
        return this.mDatabase.taskDao().getProjectTasksWithThemeLiveData(categoryID);
    }

    public Task getTask(String taskID){
        return this.mDatabase.taskDao().getTask(taskID);
    }

    public Task getTaskSilently(String taskID){
        return this.mSilentDatabase.taskDao().getTask(taskID);
    }

    public LiveData<Task> getTaskLiveData(String taskID){
        return this.mDatabase.taskDao().getTaskLiveData(taskID);
    }

    public LiveData<List<TaskAndTheme>> getTaskAndThemeByCategory(long categoryID){
        return mTaskDao.getSingleTaskAndThemeByCategory(categoryID);
    }



    // Adding new data
    public void addUserCaseStatisticSilently(UserCaseStatistic ucs){
        mSilentDatabase.databaseWriteExecutor.execute(() -> {
            mUserCaseStatisticDao.insert(ucs);
        });
    }

    public void addTask (Task newTask) {
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.insertWithReplace(newTask);
        });
    }

    public void addTaskSilently(Task newTask){
        Database.databaseWriteExecutor.execute(() -> {
            mSilentTaskDao.insertWithReplace(newTask);
        });
    }

    public void addProject (Project newProject) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.insertWithReplace(newProject);
        });
    }

    public void addProjectSilently(Project newProject){
        Database.databaseWriteExecutor.execute(() -> {
            mSilentProjectDao.insertWithReplace(newProject);
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
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.delete(removableTask);
        });
    }

    public void removeTaskByID (String id){
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteById(id);
        });
    }

    public void removeTaskByIDSilently(String id){
        mSilentDatabase.databaseWriteExecutor.execute(() -> {
            mSilentTaskDao.deleteById(id);
        });
    }

    public void removeProject (String id) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.deleteWithItemsTransaction(id);
        });
    }

    public void removeProjectByID (String id){
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.deleteById(id);
        });
    }

    public void removeProjectByIDSilently(String id){
        mSilentDatabase.databaseWriteExecutor.execute(() -> {
            mSilentProjectDao.deleteById(id);
        });
    }

    public void removeCategory (Category removableCategory) {
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.delete(removableCategory);
        });
    }

    public void removeCategoryByID (long id){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.delete(id);
        });
    }

    public void removeThemeByID (long id){
        Database.databaseWriteExecutor.execute(() -> {
            mThemeDao.deleteByID(id);
        });
    }

    public void removeCategoryWithItems(long id){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.deleteWithItemsTransaction(id);
        });
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

    public LiveData<List<TaskAndTheme>> getSingleTaskAndThemeByCategory(long categoryID) {
        return mTaskDao.getSingleTaskAndThemeByCategory(categoryID);
    }
}
