package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.repositories.MemtaskRepositoryBase;

import java.util.List;

public class MemtaskViewModelBase extends ViewModel {

    protected MemtaskRepositoryBase mRepository;
    protected LiveData<List<Task>> tasksLiveData;
    protected LiveData<List<Project>> projectsLiveData;

    public MemtaskViewModelBase() {
        super();
    }

    //Load data
    protected void loadData(Application application){
        new Thread(() -> {
            mRepository = new MemtaskRepositoryBase(application);
            tasksLiveData = mRepository.getAllTasksLive();
            projectsLiveData = mRepository.getAllProjectsLive();
        }).start();
    }

    //Getting existing data
    public Task getTask (long taskId) {
        return mRepository.getTask(taskId);
    }

    public Project getProject (long projectId) {
        return mRepository.getProject(projectId);
    }


    //Adding new data
    public void addTask (Task newTask) {
        mRepository.addTask(newTask);
    }

    public void addProject (Project newProject) {
        mRepository.addProject(newProject);
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        mRepository.removeTask(removableTask);
    }

    public void removeProject (Project removableProject) {
        mRepository.removeProject(removableProject);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        mRepository.updateTask(updatableTask);
    }

    public void updateProject (Project updatableProject) {
        mRepository.updateProject(updatableProject);
    }

    //Retrieving live data
    public LiveData<List<Task>> requestTasksData(){
        return this.tasksLiveData;
    }

    public LiveData<List<Project>> requestProjectsData(){
        return this.projectsLiveData;
    }
}
