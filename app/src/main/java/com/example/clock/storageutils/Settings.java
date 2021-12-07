package com.example.clock.storageutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class Settings {

    // 0 Calendar, 1 Category list, 2 Tasks list, 3 Statistic, 4 Settings.
    private Pair<Long, String> mCurrentCategory;
    private Pair<Boolean, String> mSetupState;
    private Pair<Long, String> mLastCategoryID;

    private SharedPreferences mSharedPref;

    public Settings(@NonNull Context context) {

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        updateData();
    }

    public void updateData(){

        String mCurrentCategoryPrefTag = "current_window";
        mCurrentCategory = new Pair<Long, String>
                (mSharedPref.getLong(mCurrentCategoryPrefTag, 1), mCurrentCategoryPrefTag);

        String mSetupStateTag = "Time";
        mSetupState = new Pair<Boolean, String>
                (mSharedPref.getBoolean(mSetupStateTag, false), mSetupStateTag);

        String mLastCategoryIDTag = "last_category_id";
        mLastCategoryID = new Pair<Long, String>
                (mSharedPref.getLong(mLastCategoryIDTag, -1), mLastCategoryIDTag);

    }

    public SharedPreferences getSharedPref(){
        return this.mSharedPref;
    }

    public long getCurrentWindow(){
        return this.mCurrentCategory.first.longValue();
    }

    public void setCurrentWindow(long id){
        SharedPreferences.Editor editor = mSharedPref.edit();
        mCurrentCategory = new Pair<Long, String>
                (id, mCurrentCategory.second);


        editor.putLong(mCurrentCategory.second, id);

        editor.commit();
    }

    public void setLastCategoryID(long id){
        SharedPreferences.Editor editor = mSharedPref.edit();
        mLastCategoryID = new Pair<Long, String>
                (id, mLastCategoryID.second);


        editor.putLong(mLastCategoryID.second, id);

        editor.commit();
    }

    public long getLastCategoryID(){
        return this.mLastCategoryID.first;
    }

    public boolean getSetupState(){
        return this.mSetupState.first;
    }

    public void setSetupState(boolean state){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(mSetupState.second, state);

        editor.commit();
    }


}
