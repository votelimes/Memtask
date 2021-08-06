package com.example.clock.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.clock.databases.Database;
import com.example.clock.dao.IdeaDao;
import com.example.clock.dao.NoteDao;
import com.example.clock.dao.ProjectDao;
import com.example.clock.model.Task;
import com.example.clock.dao.TaskDao;

import java.util.List;

public class MemtaskRepository {
    private MutableLiveData<List<Task>> taskMutableLiveData;
    private MutableLiveData<List<Task>> ideaMutableLiveData;
    private MutableLiveData<List<Task>> noteMutableLiveData;
    private MutableLiveData<List<Task>> projectMutableLiveData;

    private Database database;
    private TaskDao taskDao;
    private IdeaDao ideaDao;
    private NoteDao noteDao;
    private ProjectDao projectDao;

    public MemtaskRepository(){
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
