package com.votelimes.memtask.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.storageutils.Database;
import com.votelimes.memtask.storageutils.SilentDatabase;

public class RingViewModel extends MemtaskViewModelBase{
    private LiveData<Task> currentTask;
    private String taskID;

    public RingViewModel(Application application, Database database, SilentDatabase silentDatabase, String taskID){
        loadData(database, silentDatabase, taskID);
    }
    protected void loadData(Database database, SilentDatabase silentDatabase, String taskID){
        mRepository = new MemtaskRepositoryBase(database, silentDatabase);
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
