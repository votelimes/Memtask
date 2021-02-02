package com.example.clock;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.io.File;
import java.util.List;

public class App extends Application {

    public static App instance;
    private static LiveData<List<Alarm>> alarmsLiveData;

    private Database database;
    private AlarmDao alarmDao;


    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        this.database = Room.databaseBuilder(this, Database.class, "user_db")
                .allowMainThreadQueries()
                .build();
        alarmDao = database.alarmDao();

        alarmsLiveData = alarmDao.getAlarms();
    }

    public static App getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public long insert(Alarm alarm){
        return alarmDao.insert(alarm);
    }
    public long insertWithReplace(Alarm alarm){
        return alarmDao.insertWithReplace(alarm);
    }
    public void remove(Alarm alarm){
        alarmDao.delete(alarm);
    }
    public Alarm getById(long id){
        return alarmDao.getById(id);
    }

    public static LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}