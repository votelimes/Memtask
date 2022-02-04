package com.example.clock.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.clock.app.App;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;
import com.example.clock.storageutils.Tuple2;

import java.util.List;

public abstract class MemtaskViewModelBase extends ViewModel {

    protected MemtaskRepositoryBase mRepository;
    protected LiveData<List<Task>> tasksLiveData;
    protected LiveData<List<Project>> projectsLiveData;
    protected LiveData<List<Category>> categoriesLiveData;
    protected LiveData<List<Theme>> themesLiveData;

    public MemtaskViewModelBase() {
        super();
    }

    //Load data
    protected void loadData(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        tasksLiveData = mRepository.getAllTasksLive();
        projectsLiveData = mRepository.getAllProjectsLive();
        categoriesLiveData = mRepository.getAllCategoriesLive();
        themesLiveData = mRepository.getAllThemesLive();
    }

    //Adding new data
    public void addTask (Task newTask) {
        mRepository.addTask(newTask);
    }

    public void addTaskSilently(Task task){
        mRepository.addTaskSilently(task);
    }

    public void addProject (Project newProject) {
        mRepository.addProject(newProject);
    }

    public void addProjectSilently(Project project){
        addProjectSilently(project);
    }

    public void addCategory(Category newCategory){
        mRepository.addCategory(newCategory);
    }

    public void addTheme(Theme theme){
        mRepository.addTheme(theme);
    }

    public void addUserCaseStatisticSilently(UserCaseStatistic ucs){
        mRepository.addUserCaseStatisticSilently(ucs);
    }

    //Removing existing data
    public void removeTaskByID (String id) {
        mRepository.removeTaskByID(id);
    }

    public void removeTaskByIDSilently(String id){
        mRepository.removeTaskByIDSilently(id);
    }

    public void removeProjectByID (String id) {
        mRepository.removeProjectByID(id);
    }

    public void removeProjectByIDSilently(String id){
        mRepository.removeProjectByIDSilently(id);
    }

    public void removeCategoryByID (long id){
        mRepository.removeCategoryByID(id);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        mRepository.updateTask(updatableTask);
    }

    public void updateProject (Project updatableProject) {
        mRepository.updateProject(updatableProject);
    }

    public void updateCategory(Category updatableCategory){
        mRepository.updateCategory(updatableCategory);
    }

    //Retrieving live data
    public LiveData<List<Task>> requestTasksData(){
        return this.tasksLiveData;
    }

    public LiveData<List<Project>> requestProjectsData(){
        return this.projectsLiveData;
    }

    public LiveData<List<Category>> requestCategoriesData(){
        return this.categoriesLiveData;
    }

    public LiveData<List<Theme>> requestThemesData() {
        return this.themesLiveData;
    }
}
