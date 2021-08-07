package com.example.clock.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.clock.databases.Database;
import com.example.clock.dao.IdeaDao;
import com.example.clock.dao.NoteDao;
import com.example.clock.dao.ProjectDao;
import com.example.clock.model.Idea;
import com.example.clock.model.Note;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.dao.TaskDao;

import java.util.List;

public class MemtaskRepository {
    private MutableLiveData<List<Task>> taskMutableLiveData;
    private MutableLiveData<List<Idea>> ideaMutableLiveData;
    private MutableLiveData<List<Note>> noteMutableLiveData;
    private MutableLiveData<List<Project>> projectMutableLiveData;

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

    // Adding new data
    public void addTask (Task newTask) {
        taskDao.insertWithReplace(newTask);
    }

    public void addIdea (Idea newIdea) {
        ideaDao.insertWithReplace(newIdea);
    }

    public void addNote (Note newNote) {
        noteDao.insertWithReplace(newNote);
    }

    public void addProject (Project newProject) {
        projectDao.insertWithReplace(newProject);
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        taskDao.delete(removableTask);
    }

    public void removeIdea (Idea removableIdea) {
        ideaDao.delete(removableIdea);
    }

    public void removeNote (Note removableNote) {
        noteDao.delete(removableNote);
    }

    public void removeProject (Project removableProject) {
        projectDao.delete(removableProject);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        taskDao.update(updatableTask);
    }

    public void updateIdea (Idea updatableIdea) {
        ideaDao.update(updatableIdea);
    }

    public void updateNote (Note updatableNote) {
        noteDao.update(updatableNote);
    }

    public void updateProject (Project updatableProject) {
        projectDao.update(updatableProject);
    }

    //For binding observables
    public LiveData<List<Task>> requestTaskData(){
        final LiveData<List<Task>> liveData = taskMutableLiveData;

        return liveData;
    }

    public LiveData<List<Task>> requestIdeaData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }

    public LiveData<List<Task>> requestNoteData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }

    public LiveData<List<Task>> requestProjectData(){
        final LiveData<List<Task>> liveData = new MutableLiveData<>();

        return liveData;
    }
}
