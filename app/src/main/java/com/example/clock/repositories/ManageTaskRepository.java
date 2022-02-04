package com.example.clock.repositories;

import android.app.Application;


import com.example.clock.model.Task;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;

public class ManageTaskRepository extends MemtaskRepositoryBase {

    private Task mManagingTask;

    public ManageTaskRepository(Application application, Database database, SilentDatabase silentDatabase, Task managingTask) {
        super(application, database, silentDatabase);
        this.mManagingTask = managingTask;
    }

    public boolean getManagingTaskVibrate(){
        return this.mManagingTask.isVibrate();
    }

    public String getManagingTaskDescription(){
        return this.mManagingTask.getDescription();
    }

    public void setManagingTaskVibrate(boolean isVibrate){
        this.mManagingTask.setVibrate(isVibrate);
    }

    public void setManagingTaskDescription(String description){
        this.mManagingTask.setDescription(description);
    }
}
