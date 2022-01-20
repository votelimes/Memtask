package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.clock.app.App;
import com.example.clock.model.Project;
import com.example.clock.model.ProjectAndTheme;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseBase;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.Tuple2;
import com.example.clock.storageutils.Tuple3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends MemtaskViewModelBase {
    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;

    private int sortType;
    private boolean shouldUpdate;

    MainViewModel(Application application, Database database, Database silentDatabase){
        loadData(application, database, silentDatabase);

        shouldUpdate = true;
    }

    protected void loadData(Application application, Database database, Database silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        categoriesLiveData = mRepository.getAllCategoriesLive();
    }

    public void clean(){

    }

    public void removeCategoryWithItems(long id){
        mRepository.removeCategoryWithItems(id);
    }
}
