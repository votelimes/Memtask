package com.example.clock.app;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.clock.storageutils.Settings;

public class App extends Application {

    public static App instance;
    public static Settings mSettings;
    SharedPreferences.OnSharedPreferenceChangeListener settingsUpdateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

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
}