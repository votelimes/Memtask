package com.example.clock.app;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.clock.R;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.Settings;
import com.example.clock.storageutils.SilentDatabase;

public class App extends Application {

    public static App instance;
    private static Settings mSettings;
    private static Database mDatabase;
    private static SilentDatabase mSilentDatabase;
    SharedPreferences.OnSharedPreferenceChangeListener settingsUpdateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mDatabase = Database.getDatabase(this);
        mSilentDatabase = SilentDatabase.getDatabase(this);

        mSettings = new Settings(getApplicationContext());

        settingsUpdateListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                mSettings.updateData(instance);

                /*int darkMode = getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;*/
                applyTheme();
            }
        };

        mSettings.getSharedPref().registerOnSharedPreferenceChangeListener(settingsUpdateListener);
        applyTheme();
    }

    public static App getInstance() {
        return instance;
    }

    public static Database getDatabase(){
        return mDatabase;
    }

    public static SilentDatabase getSilentDatabase(){
        return mSilentDatabase;
    };

    public static Settings getSettings(){
        return mSettings;
    }

    private void applyTheme(){
        String themeModes[] = getResources()
                .getStringArray(R.array.preference_light_theme_value);

        if(getSettings().getUseDarkTheme().equals(themeModes[0])){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        else if(getSettings().getUseDarkTheme().equals(themeModes[1])){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public boolean isTesting(){
        return mSettings.TESTING;
    }
}