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

    public ManageTaskViewModel(Application application, Database database, Project managingProject){
        mManagingTaskRepository = new Observer(managingProject);
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
        private Project mManagingProject;

        Observer(@NonNull Task managingTask){
            this.mManagingTask = managingTask;
            this.mManagingProject = null;
        }

        Observer(@NonNull Project managingProject){
            this.mManagingProject = managingProject;
            this.mManagingTask = null;
        }

        @Bindable
        public String getTaskName() {
            if(mManagingTask != null){
                return this.mManagingTask.getName();
            }
            else if(mManagingProject != null){
                return this.mManagingProject.getName();
            }

            return "WRONG CASE PASS";
        }

        @Bindable
        public String getTaskDescription(){
            if(mManagingTask != null){
                return this.mManagingTask.getDescription();
            }
            else if(mManagingProject != null){
                return this.mManagingProject.getDescription();
            }

            return "WRONG CASE PASS";
        }

        @Bindable
        public String getTaskRepeatModeString(){
            if(mManagingTask != null){
                return this.mManagingTask.getRepeatModeString();
            }

            return "WRONG CASE PASS";
        }

        public void setTaskName(String name) {
            if(mManagingTask != null){
                this.mManagingTask.setName(name);
            }
            else if(mManagingProject != null){
                this.mManagingProject.setName(name);
            }
            notifyPropertyChanged(BR.taskDescription);
        }

        public void setTaskDescription(String description){
            if(mManagingTask != null){
                this.mManagingTask.setDescription(description);
            }
            else if(mManagingProject != null){
                this.mManagingProject.setDescription(description);
            }
            notifyPropertyChanged(BR.taskDescription);
        }

        public void setRepeatModeString(String repeatMode){
            if(mManagingTask != null){
                this.mManagingTask.setRepeatModeString(repeatMode);
            }

            notifyPropertyChanged(BR.taskRepeatModeString);
        }

        public boolean isTaskMode(){
            return mManagingTask != null;
        }

        public boolean isProjectMode(){
            return mManagingProject != null;
        }

    }


}
