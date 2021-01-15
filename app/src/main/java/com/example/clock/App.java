package com.example.clock;

import android.app.Application;

import androidx.room.Room;

import java.io.File;

public class App extends Application {

    public static App instance;

    private Database database;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;


        this.database = Room.databaseBuilder(this, Database.class, "user_db")
                .allowMainThreadQueries()
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }
}