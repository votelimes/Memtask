package com.example.clock.storageutils;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.clock.dao.CategoryDao;
import com.example.clock.dao.ProjectDao;
import com.example.clock.dao.TaskDao;
import com.example.clock.dao.ThemeDao;
import com.example.clock.dao.UserCaseStatisticDao;
import com.example.clock.model.Category;
import com.example.clock.model.Theme;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.UserCaseStatistic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Task.class, Project.class, Category.class, Theme.class, UserCaseStatistic.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();
    public abstract CategoryDao categoryDao();
    public abstract ThemeDao themeDao();
    public abstract UserCaseStatisticDao userCaseStatisticDao();

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
                    ).allowMainThreadQueries()
                     .build();
                }
            }
        }
        return INSTANCE;
    }
}
