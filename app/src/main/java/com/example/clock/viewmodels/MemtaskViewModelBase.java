package com.example.clock.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.clock.model.Idea;
import com.example.clock.model.Note;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.repositories.MemtaskRepository;

import java.util.List;

public class MemtaskViewModelBase extends ViewModel {
    public MemtaskRepository repository;

    MemtaskViewModelBase(){
        super();
        repository = new MemtaskRepository();
    }

    //Adding new data
    public void addTask (Task newTask) {
        repository.addTask(newTask);
    }

    public void addIdea (Idea newIdea) {
        repository.addIdea(newIdea);
    }

    public void addNote (Note newNote) {
        repository.addNote(newNote);
    }

    public void addProject (Project newProject) {
        repository.addProject(newProject);
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        repository.removeTask(removableTask);
    }

    public void removeIdea (Idea removableIdea) {
        repository.removeIdea(removableIdea);
    }

    public void removeNote (Note removableNote) {
        repository.removeNote(removableNote);
    }

    public void removeProject (Project removableProject) {
        repository.removeProject(removableProject);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        repository.updateTask(updatableTask);
    }

    public void updateIdea (Idea updatableIdea) {
        repository.updateIdea(updatableIdea);
    }

    public void updateNote (Note updatableNote) {
        repository.updateNote(updatableNote);
    }

    public void updateProject (Project updatableProject) {
        repository.updateProject(updatableProject);
    }

    //Retrieving live data
    private LiveData<List<Task>> requestTaskData(){
        return repository.requestTaskData();
    }

    private LiveData<List<Task>> requestIdeaData(){
        return repository.requestIdeaData();
    }

    private LiveData<List<Task>> requestNoteData(){
        return repository.requestNoteData();
    }

    private LiveData<List<Task>> requestProjectData(){
        return repository.requestProjectData();
    }
}
