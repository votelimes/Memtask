package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.storageutils.Database;

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
            if(mParams[1].getClass() == Task.class){
                return (T) new ManageTaskViewModel(mApplication, (Database) mParams[0], (Task) mParams[1]);
            }
            else if(mParams[1].getClass() == Project.class){
                return (T) new ManageTaskViewModel(mApplication, (Database) mParams[0], (Project) mParams[1]);
            }
            return null;
        }
        else if (modelClass == ManageCategoryViewModel.class) {
            return (T) new ManageCategoryViewModel(mApplication, (Database) mParams[0], (Category) mParams[1]);
        }
        else if(modelClass == MainViewModel.class){
            return (T) new MainViewModel(mApplication, (Database) mParams[0]);
        }
        else {
            return super.create(modelClass);
        }
    }
}
