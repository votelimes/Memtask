package com.example.clock.viewmodels;


import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import androidx.lifecycle.LiveData;


import com.example.clock.BR;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;

import java.util.List;

public class ManageTaskViewModel extends MemtaskViewModelBase {

    public Observer mManagingTaskRepository;

    public ManageTaskViewModel(Application application, Database database, Task managingTask){
        mManagingTaskRepository = new Observer(managingTask);
        loadData(application, database);
    }

    public  LiveData<List<Task>> getTasksData(Application application, Database database){
        if(mRepository == null){
            loadData(application, database);
        }
        return this.tasksLiveData;
    }

    public  LiveData<List<Project>> getProjectsData(Application application, Database database){
        if(mRepository == null){
            loadData(application, database);
        }
        return this.projectsLiveData;
    }

    public void saveChanges(){
        this.mRepository.addTask(this.mManagingTaskRepository.mManagingTask);
    }

    public static class Observer extends BaseObservable{

        private Task mManagingTask;

        Observer(@NonNull Task managingTask){
            this.mManagingTask = managingTask;
        }

        @Bindable
        public String getTaskName() {
            return this.mManagingTask.getName();
        }

        @Bindable
        public String getTaskDescription(){
            return this.mManagingTask.getDescription();
        }

        @Bindable
        public String getTaskRepeatModeString(){
            return this.mManagingTask.getRepeatModeString();
        }

        public void setTaskName(String name) {
            this.mManagingTask.setName(name);
            notifyPropertyChanged(BR.taskDescription);
        }

        public void setTaskDescription(String description){
            this.mManagingTask.setDescription(description);
            notifyPropertyChanged(BR.taskDescription);
        }

        public void setRepeatModeString(String repeatMode){
            this.mManagingTask.setRepeatModeString(repeatMode);
            notifyPropertyChanged(BR.taskRepeatModeString);
        }

    }
}
