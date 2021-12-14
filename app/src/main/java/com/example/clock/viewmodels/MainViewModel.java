package com.example.clock.viewmodels;

import android.app.Application;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends MemtaskViewModelBase {

    private MediatorLiveData<Tuple3<List<Task>, List<Project>, List<Theme>>> mMergedLiveData;
    public LiveData<Tuple3<List<Task>, List<Project>, List<Theme>>> intermediate;

    MainViewModel(Application application, Database database){
        loadData(application, database);
        intermediate =  LiveDataTransformations.ifNotNull(tasksLiveData, projectsLiveData, themesLiveData);


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

    public List<Project> getProjectsByCategory(long categoryID){

        List<Project> projectsList = projectsLiveData.getValue();
        if(projectsList == null)
            return null;

        List<Project> filteredByCategoryProjects = projectsList
                .stream()
                .filter(c -> c.getCategoryId() == categoryID)
                .collect(Collectors.toList());

        return filteredByCategoryProjects;
    }

    public List<Task> getTasksByParent(long parentID){
        List<Task> tasksList = tasksLiveData.getValue();
        if(tasksList == null)
            return null;

        List<Task> filteredByParentTasks = tasksList
                .stream()
                .filter(c -> c.getParent() == parentID)
                .collect(Collectors.toList());

        return filteredByParentTasks;
    }

    public List<Theme> getThemes(){
        return themesLiveData.getValue();
    }
}
