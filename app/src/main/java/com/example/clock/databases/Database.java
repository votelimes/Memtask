package com.example.clock.databases;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.clock.dao.IdeaDao;
import com.example.clock.dao.NoteDao;
import com.example.clock.dao.ProjectDao;
import com.example.clock.dao.TaskDao;
import com.example.clock.model.Idea;
import com.example.clock.model.Note;
import com.example.clock.model.Project;
import com.example.clock.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Task.class, Idea.class, Note.class, Project.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract IdeaDao ideaDao();
    public abstract NoteDao noteDao();
    public abstract ProjectDao projectDao();

    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            Database.class,
                            "memtask_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
