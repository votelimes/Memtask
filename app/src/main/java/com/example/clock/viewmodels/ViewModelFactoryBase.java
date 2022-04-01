package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.model.Category;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;

public class ViewModelFactoryBase extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private Object[] mParams;

    public ViewModelFactoryBase(Application application, Object... params) {
        mApplication = application;
        mParams = params;
    }

    public ViewModelFactoryBase(Application application) {
        mApplication = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass == ManageTaskViewModel.class) {
            return (T) new ManageTaskViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1], (int) mParams[2], (String) mParams[3], (long) mParams[4], (String) mParams[5]);
        }
        else if (modelClass == ManageCategoryViewModel.class) {
            return (T) new ManageCategoryViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1], (Category) mParams[2]);
        }
        else if(modelClass == MainViewModel.class){
            return (T) new MainViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1]);
        }
        else if(modelClass == CategoryActivitiesViewModel.class){
            return (T) new CategoryActivitiesViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1]);
        }
        else if(modelClass == CalendarViewModel.class){
            return (T) new CalendarViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1]);
        }
        else if(modelClass == StatisticViewModel.class){
            return (T) new StatisticViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1]);
        }
        else if(modelClass == RingViewModel.class){
            return (T) new RingViewModel(mApplication, (Database) mParams[0], (SilentDatabase) mParams[1], (String) mParams[2]);
        }
        else {
            return super.create(modelClass);
        }
    }
}
