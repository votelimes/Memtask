package com.example.clock.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

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

    public MemtaskRepository(Application application){
        this.taskMutableLiveData = new MutableLiveData<>();
        this.ideaMutableLiveData = new MutableLiveData<>();
        this.noteMutableLiveData = new MutableLiveData<>();
        this.projectMutableLiveData = new MutableLiveData<>();

        this.database = Room.databaseBuilder(application, Database.class, "memtask_db")
                .allowMainThreadQueries()
                .build();
    }

    //Getting existing data
    public Task getTask (long taskId) {
        return taskDao.getById(taskId);
    }

    public Idea getIdea (long ideaId) {
        return ideaDao.getById(ideaId);
    }

    public Note getNote (long noteId) {
        return noteDao.getById(noteId);
    }

    public Project getProject (long projectId) {
        return projectDao.getById(projectId);
    }

    // Adding new data
    public void addTask (Task newTask) {
        Database.databaseWriteExecutor.execute(() -> {
            taskDao.insertWithReplace(newTask);
        });
    }

    public void addIdea (Idea newIdea) {
        Database.databaseWriteExecutor.execute(() -> {
            ideaDao.insertWithReplace(newIdea);
        });
    }

    public void addNote (Note newNote) {
        Database.databaseWriteExecutor.execute(() -> {
            noteDao.insertWithReplace(newNote);
        });
    }

    public void addProject (Project newProject) {
        Database.databaseWriteExecutor.execute(() -> {
            projectDao.insertWithReplace(newProject);
        });
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
        Database.databaseWriteExecutor.execute(() -> {
            taskDao.update(updatableTask);
        });
    }

    public void updateIdea (Idea updatableIdea) {
        Database.databaseWriteExecutor.execute(() -> {
            ideaDao.update(updatableIdea);
        });
    }

    public void updateNote (Note updatableNote) {
        Database.databaseWriteExecutor.execute(() -> {
            noteDao.update(updatableNote);
        });
    }

    public void updateProject (Project updatableProject) {
        Database.databaseWriteExecutor.execute(() -> {
            projectDao.update(updatableProject);
        });
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
