package com.example.clock.viewmovel;

import androidx.lifecycle.ViewModel;

import com.example.clock.repositories.MemtaskRepository;

public class MemtaskViewModel extends ViewModel {
    public MemtaskRepository repository;

    MemtaskViewModel(){
        repository = new MemtaskRepository();


    }
}
