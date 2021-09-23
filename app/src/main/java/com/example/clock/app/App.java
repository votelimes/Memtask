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
    public static Settings Settings;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;

        Settings = new Settings(getApplicationContext());
    }

    public static App getInstance() {
        return instance;
    }
}