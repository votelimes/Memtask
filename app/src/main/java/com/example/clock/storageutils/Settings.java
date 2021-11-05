package com.example.clock.storageutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class Settings {

    private Pair<Long, String> mCurrentCategory;
    private Pair<Boolean, String> mSetupState;

    private SharedPreferences mSharedPref;

    public Settings(@NonNull Context context) {

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        updateData();
    }

    public void updateData(){

        String mCurrentCategoryPrefTag = "current_category";
        mCurrentCategory = new Pair<Long, String>
                (mSharedPref.getLong(mCurrentCategoryPrefTag, -1), mCurrentCategoryPrefTag);

        String mSetupStateTag = "Time";
        mSetupState = new Pair<Boolean, String>
                (mSharedPref.getBoolean(mSetupStateTag, false), mSetupStateTag);

    }

    public SharedPreferences getSharedPref(){
        return this.mSharedPref;
    }

    public long getCurrentCategory(){
        return this.mCurrentCategory.first.longValue();
    }

    public void setCurrentCategory(long id){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putLong(mCurrentCategory.second, id);

        editor.commit();
    }

    public boolean getSetupState(){
        return this.mSetupState.first.booleanValue();
    }

    public void setSetupState(boolean state){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(mSetupState.second, state);

        editor.commit();
    }
}
