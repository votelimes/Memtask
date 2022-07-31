package com.votelimes.memtask.storageutils;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.votelimes.memtask.dao.CategoryDao;
import com.votelimes.memtask.dao.ProjectDao;
import com.votelimes.memtask.dao.TaskDao;
import com.votelimes.memtask.dao.ThemeDao;
import com.votelimes.memtask.dao.UserCaseStatisticDao;
import com.votelimes.memtask.model.Category;
import com.votelimes.memtask.model.Project;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.model.UserCaseStatistic;

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
    private static final int NUMBER_OF_THREADS = 8;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            Database.class,
                            "memtask_db"
                    )
                     .build();
                }
                INSTANCE.getOpenHelper().getWritableDatabase().setMaxSqlCacheSize(40);
                INSTANCE.getOpenHelper().getWritableDatabase().setPageSize(8192);
            }
        }
        return INSTANCE;
    }
}
