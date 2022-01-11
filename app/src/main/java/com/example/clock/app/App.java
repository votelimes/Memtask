package com.example.clock.app;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.Settings;

public class App extends Application {

    public static App instance;
    private static Settings mSettings;
    private static Database mDatabase;
    private static Database mSilentDatabase;
    SharedPreferences.OnSharedPreferenceChangeListener settingsUpdateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mDatabase = Room.databaseBuilder(this, Database.class, "memtask_db")
                .build();
        mSilentDatabase = mDatabase = Room.databaseBuilder(this, Database.class, "memtask_db")
                .build();

        mSettings = new Settings(getApplicationContext());

        settingsUpdateListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                mSettings.updateData();
            }
        };

        mSettings.getSharedPref().registerOnSharedPreferenceChangeListener(settingsUpdateListener);
    }

    public static App getInstance() {
        return instance;
    }

    public static Database getDatabase(){
        return mDatabase;
    }

    public static Database getSilentDatabase(){
        return mSilentDatabase;
    };

    public static Settings getSettings(){
        return mSettings;
    }
}