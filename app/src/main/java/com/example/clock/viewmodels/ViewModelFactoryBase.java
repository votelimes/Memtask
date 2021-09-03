package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.model.Task;

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
            return (T) new ManageTaskViewModel(mApplication, (Task) mParams[0]);
        } else {
            return super.create(modelClass);
        }
    }
}
