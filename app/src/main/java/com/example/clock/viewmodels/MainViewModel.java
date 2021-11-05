package com.example.clock.viewmodels;

import android.app.Application;

import com.example.clock.storageutils.Database;

public class MainViewModel extends MemtaskViewModelBase {

    private long mCurrentCategoryID;

    MainViewModel(Application application, Database database){
        loadData(application, database);

        mCurrentCategoryID = -1;
    }

    public long getCurrentCategoryID() {
        return mCurrentCategoryID;
    }

    public void setCurrentCategoryID(long mCurrentCategoryID) {
        this.mCurrentCategoryID = mCurrentCategoryID;
    }
}
