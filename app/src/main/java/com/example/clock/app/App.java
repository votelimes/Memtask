package com.example.clock.app;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import com.example.clock.app.Settings;

import com.example.clock.data.Alarm;
import com.example.clock.data.AlarmDao;
import com.example.clock.data.Database;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class App extends Application {

    public static App instance;
    private static LiveData<List<Alarm>> alarmsLiveData;

    private Database database;
    private AlarmDao alarmDao;
    public static Settings Settings;

    //private static Map<String, Integer> color; // Theme colors storage

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;

        this.database = Room.databaseBuilder(this, Database.class, "user_db")
                .allowMainThreadQueries()
                .build();
        alarmDao = database.alarmDao();

        alarmsLiveData = alarmDao.getAlarmsLive();
        Settings = new Settings(this.getApplicationContext());
    }

    public static App getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public void insert(Alarm alarm){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            alarmDao.insert(alarm);
        });
    }
    public void insertWithReplace(Alarm alarm){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            alarmDao.insertWithReplace(alarm);
        });
    }
    public void remove(Alarm alarm){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            try {
                alarmDao.delete(alarm);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void removeById(long id){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            try {
                alarmDao.deleteById(id);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public Alarm getById(long id){
        return alarmDao.getById(id);
    }
    public void update(Alarm alarm){
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            try {
                alarmDao.update(alarm);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public static LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}