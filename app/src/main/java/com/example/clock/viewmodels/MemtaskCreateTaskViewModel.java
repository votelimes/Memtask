package com.example.clock.viewmodels;

import android.app.Application;

import androidx.databinding.Observable;

import com.example.clock.databases.Database;
import com.example.clock.model.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MemtaskCreateTaskViewModel extends MemtaskViewModelBase implements Observable {

    private Task managingTask;

    public MemtaskCreateTaskViewModel(Application application, Task managingTask){
        super(application);

        this.managingTask = managingTask;
    }


    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }
}
