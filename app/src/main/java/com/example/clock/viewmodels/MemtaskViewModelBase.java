package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.clock.model.Idea;
import com.example.clock.model.Note;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.repositories.MemtaskRepositoryBase;

import java.util.List;

public class MemtaskViewModelBase extends ViewModel {
    protected MemtaskRepositoryBase mRepository;

    public MemtaskViewModelBase() {
        super();
    }

    //Load data
    protected void loadData(Application application){
        new Thread(() -> {
            mRepository = new MemtaskRepositoryBase(application);
        }).start();
    }

    //Getting existing data
    public Task getTask (long taskId) {
        return mRepository.getTask(taskId);
    }

    public Idea getIdea (long ideaId) {
        return mRepository.getIdea(ideaId);
    }

    public Note getNote (long noteId) {
        return mRepository.getNote(noteId);
    }

    public Project getProject (long projectId) {
        return mRepository.getProject(projectId);
    }


    //Adding new data
    public void addTask (Task newTask) {
        mRepository.addTask(newTask);
    }

    public void addIdea (Idea newIdea) {
        mRepository.addIdea(newIdea);
    }

    public void addNote (Note newNote) {
        mRepository.addNote(newNote);
    }

    public void addProject (Project newProject) {
        mRepository.addProject(newProject);
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        mRepository.removeTask(removableTask);
    }

    public void removeIdea (Idea removableIdea) {
        mRepository.removeIdea(removableIdea);
    }

    public void removeNote (Note removableNote) {
        mRepository.removeNote(removableNote);
    }

    public void removeProject (Project removableProject) {
        mRepository.removeProject(removableProject);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        mRepository.updateTask(updatableTask);
    }

    public void updateIdea (Idea updatableIdea) {
        mRepository.updateIdea(updatableIdea);
    }

    public void updateNote (Note updatableNote) {
        mRepository.updateNote(updatableNote);
    }

    public void updateProject (Project updatableProject) {
        mRepository.updateProject(updatableProject);
    }

    //Retrieving live data
    private LiveData<List<Task>> requestTaskData(Application application){
        if(mRepository == null){
            loadData(application);
        }
        return mRepository.requestTaskData();
    }

    private LiveData<List<Task>> requestIdeaData(){
        return mRepository.requestIdeaData();
    }

    private LiveData<List<Task>> requestNoteData(){
        return mRepository.requestNoteData();
    }

    private LiveData<List<Task>> requestProjectData(){
        return mRepository.requestProjectData();
    }
}
