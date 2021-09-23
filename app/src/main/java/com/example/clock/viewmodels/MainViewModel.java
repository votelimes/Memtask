package com.example.clock.viewmodels;

import android.app.Application;

public class MainViewModel extends MemtaskViewModelBase {


    MainViewModel(Application application){
        loadData(application);
    }


}
