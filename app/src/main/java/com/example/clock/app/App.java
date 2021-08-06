package com.example.clock.app;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.clock.model.Task;
import com.example.clock.dao.TaskDao;
import com.example.clock.databases.Database;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class App extends Application {

    public static App instance;
    private static LiveData<List<Task>> alarmsLiveData;

    private Database database;
    private TaskDao taskDao;
    public static Settings Settings;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;

        this.database = Room.databaseBuilder(this, Database.class, "memtask_db")
                .allowMainThreadQueries()
                .build();
        taskDao = database.taskDao();

        alarmsLiveData = taskDao.getAlarmsLive();
        Settings = new Settings(this.getApplicationContext());
    }

    public static App getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public void insert(Task task){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            taskDao.insert(task);
        });
    }
    public void insertWithReplace(Task task){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            taskDao.insertWithReplace(task);
        });
    }
    public void remove(Task task){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            try {
                taskDao.delete(task);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void removeById(long id){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            try {
                taskDao.deleteById(id);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public Task getById(long id){
        return taskDao.getById(id);
    }
    public void update(Task task){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            try {
                taskDao.update(task);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public static LiveData<List<Task>> getAlarmsLiveData() {
        return alarmsLiveData;
    }

}