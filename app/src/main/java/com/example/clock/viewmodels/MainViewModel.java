package com.example.clock.viewmodels;

import android.app.Application;

public class MainViewModel extends MemtaskViewModelBase {

    private long mCurrentCategoryID;

    MainViewModel(Application application){
        loadData(application);

        mCurrentCategoryID = -1;
    }

    public long getCurrentCategoryID() {
        return mCurrentCategoryID;
    }

    public void setCurrentCategoryID(long mCurrentCategoryID) {
        this.mCurrentCategoryID = mCurrentCategoryID;
    }
}
