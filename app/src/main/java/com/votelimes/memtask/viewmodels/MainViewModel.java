package com.votelimes.memtask.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.storageutils.Database;
import com.votelimes.memtask.storageutils.SilentDatabase;

public class MainViewModel extends MemtaskViewModelBase {
    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;

    private int sortType;
    private boolean shouldUpdate;

    MainViewModel(Application application, Database database, SilentDatabase silentDatabase){
        loadData(application, database, silentDatabase);
        shouldUpdate = true;
    }

    protected void loadData(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(database, silentDatabase);
        categoriesLiveData = mRepository.getAllCategoriesLive();
    }

    public void clean(){

    }

    public void removeCategoryWithItems(String id){
        mRepository.removeCategoryWithItems(id);
    }

    public void syncGCREAD(Context context, LifecycleOwner lco){
        mRepository.synchronizeGCCalendars(context, lco);
    }
}
