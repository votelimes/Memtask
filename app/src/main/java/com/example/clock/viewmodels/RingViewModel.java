package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.clock.model.Task;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;

public class RingViewModel extends MemtaskViewModelBase{
    private LiveData<Task> currentTask;
    private String taskID;

    public RingViewModel(Application application, Database database, SilentDatabase silentDatabase, String taskID){
        loadData(application, database, silentDatabase, taskID);
    }
    protected void loadData(Application application, Database database, SilentDatabase silentDatabase, String taskID){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        this.taskID = taskID;
        currentTask = mRepository.getTaskLiveData(taskID);
        projectsLiveData = null;
        categoriesLiveData = null;
        themesLiveData = null;
    }

    public LiveData<Task> getCurrentTask() {
        return currentTask;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
}
