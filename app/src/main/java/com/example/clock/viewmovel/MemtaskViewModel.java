package com.example.clock.viewmovel;

import androidx.lifecycle.ViewModel;

import com.example.clock.data.UserCaseRepo;

public class MemtaskViewModel extends ViewModel {
    public UserCaseRepo repository;

    MemtaskViewModel(){
        repository = new UserCaseRepo();


    }
}
