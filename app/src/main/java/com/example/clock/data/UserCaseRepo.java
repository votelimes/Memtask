package com.example.clock.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class UserCaseRepo {
    private MutableLiveData<List<Task>> taskMutableLiveData;
    private MutableLiveData<List<Task>> ideaMutableLiveData;
    private MutableLiveData<List<Task>> noteMutableLiveData;
    private MutableLiveData<List<Task>> projectMutableLiveData;

    public UserCaseRepo(){
        taskMutableLiveData = new MutableLiveData<>();
        ideaMutableLiveData = new MutableLiveData<>();
        noteMutableLiveData = new MutableLiveData<>();
        projectMutableLiveData = new MutableLiveData<>();


    }

    private LiveData<List<Task>> requestTaskData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }

    private LiveData<List<Task>> requestIdeaData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }

    private LiveData<List<Task>> requestNoteData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }

    private LiveData<List<Task>> requestProjectData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }
}
