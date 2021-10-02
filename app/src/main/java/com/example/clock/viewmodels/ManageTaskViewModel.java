package com.example.clock.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;


import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.repositories.MemtaskRepositoryBase;

import java.util.List;

public class ManageTaskViewModel extends MemtaskViewModelBase {

    public Observer mManagingTaskRepository;

    private MemtaskRepositoryBase mRepository;

    public ManageTaskViewModel(Application application, Task managingTask){
        mManagingTaskRepository = new Observer(managingTask);
        loadData(application);
    }

    public  LiveData<List<Task>> getTasksData(Application application){
        if(mRepository == null){
            loadData(application);
        }
        return this.tasksLiveData;
    }

    public  LiveData<List<Project>> getProjectsData(Application application){
        if(mRepository == null){
            loadData(application);
        }
        return this.projectsLiveData;
    }

    public void onSaveClick(View view){
        AutoCompleteTextView newView = (AutoCompleteTextView) view;
        String selectedValue = newView.getText().toString();
    }

    public static class Observer extends BaseObservable{

        private Task mManagingTask;

        Observer(@NonNull Task managingTask){
            this.mManagingTask = managingTask;
        }

        @Bindable
        public String getTaskDescription(){
            return this.mManagingTask.getDescription();
        }

        @Bindable
        public String getTaskRepeatModeString(){
            return this.mManagingTask.getRepeatModeString();
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
