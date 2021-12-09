package com.example.clock.viewmodels;

import android.app.Application;

import com.example.clock.model.Task;
import com.example.clock.storageutils.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends MemtaskViewModelBase {

    MainViewModel(Application application, Database database){
        loadData(application, database);
    }

    public List<Task> getTasksByCategory(long categoryID){

        List<Task> tasksList = tasksLiveData.getValue();
        if(tasksList == null)
            return null;

        List<Task> filteredByCategoryTasks = tasksList
                .stream()
                .filter(c -> c.getCategoryId() == categoryID)
                .collect(Collectors.toList());

        return filteredByCategoryTasks;
    }
}
