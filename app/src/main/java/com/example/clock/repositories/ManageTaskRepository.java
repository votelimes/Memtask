package com.example.clock.repositories;

import android.app.Application;


import com.example.clock.model.Task;
import com.example.clock.storageutils.Database;

public class ManageTaskRepository extends MemtaskRepositoryBase {

    private Task mManagingTask;

    public ManageTaskRepository(Application application, Database database, Task managingTask) {
        super(application, database);

        this.mManagingTask = managingTask;
    }

    public String getManagingTaskRepeatModeString(){

        return this.mManagingTask.getRepeatModeString();
    }

    public boolean getManagingTaskVibrate(){
        return this.mManagingTask.isVibrate();
    }

    public String getManagingTaskDescription(){
        return this.mManagingTask.getDescription();
    }

    public void setManagingTaskRepeatModeString(String repeatMode){
        this.mManagingTask.setRepeatModeString(repeatMode);
    }

    public void setManagingTaskVibrate(boolean isVibrate){
        this.mManagingTask.setVibrate(isVibrate);
    }

    public void setManagingTaskDescription(String description){
        this.mManagingTask.setDescription(description);
    }
}
