package com.example.clock.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.clock.databases.Database;
import com.example.clock.dao.ProjectDao;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.dao.TaskDao;

import java.util.List;

public class MemtaskRepositoryBase {

    private Database database;
    private TaskDao taskDao;
    private ProjectDao projectDao;

    public MemtaskRepositoryBase(Application application){
        this.database = Room.databaseBuilder(application, Database.class, "memtask_db")
                .allowMainThreadQueries()
                .build();

        taskDao = database.taskDao();
        projectDao = database.projectDao();
    }

    //Getting existing data
    public Task getTask (long taskId) {
        return taskDao.getById(taskId);
    }

    public LiveData<List<Task>> getAllTasksLive(){
        return this.database.taskDao().getTasksLiveData();
    }

    public Project getProject (long projectId) {
        return projectDao.getById(projectId);
    }

    public LiveData<List<Project>> getAllProjectsLive(){
        return this.database.projectDao().getProjectsLiveData();
    }

    // Adding new data
    public void addTask (Task newTask) {
        Database.databaseWriteExecutor.execute(() -> {
            taskDao.insertWithReplace(newTask);
        });
    }

    public void addProject (Project newProject) {
        Database.databaseWriteExecutor.execute(() -> {
            projectDao.insertWithReplace(newProject);
        });
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        taskDao.delete(removableTask);
    }

    public void removeProject (Project removableProject) {
        projectDao.delete(removableProject);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        Database.databaseWriteExecutor.execute(() -> {
            taskDao.update(updatableTask);
        });
    }

    public void updateProject (Project updatableProject) {
        Database.databaseWriteExecutor.execute(() -> {
            projectDao.update(updatableProject);
        });
    }
}
