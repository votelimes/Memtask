package com.votelimes.memtask.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.votelimes.memtask.model.Project;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskAndTheme;
import com.votelimes.memtask.model.TaskData;
import com.votelimes.memtask.model.TaskNotificationData;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

@Dao
public abstract class TaskDao extends BaseDao<Task> {

    @Query("SELECT * FROM task_table")
    public abstract List <Task> getAll();

    //@Query("SELECT * FROM task_table ORDER BY timeInMillis DESC")
    @Query("SELECT * FROM task_table ORDER by mName ASC")
    public abstract LiveData<List<Task>> getTasksLiveData();

    @Query("SELECT * FROM task_table ORDER by mName ASC")
    public abstract List<Task> getAllTasks();

    @Query("DELETE FROM task_table")
    public abstract int clear();

    @Query("DELETE FROM task_table WHERE task_table.task_ID = :id")
    public abstract int deleteById(String id);

    @Query("SELECT * FROM task_table WHERE mNotificationStartMillis >= :startMillis AND mNotificationStartMillis < :endMillis ORDER by mNotificationStartMillis ASC")
    public abstract LiveData<List<Task>> getTasksLiveDataByNotification(long startMillis, long endMillis);

    @Query("SELECT task_table.*, theme_table.*, category_table.categoryName FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID LEFT JOIN category_table ON task_table.categoryId = category_table.categoryId"
            + " WHERE (task_table.mNotificationStartMillis >= :startMillis AND task_table.mNotificationStartMillis < :endMillis) OR (task_table.repeatMode > 0 AND task_table.mNotificationStartMillis > 0) ORDER BY mNotificationStartMillis ASC")
    public abstract LiveData<List<TaskData>> getTasksWithThemeByNotification(long startMillis, long endMillis);

    @Query("SELECT task_table.*, theme_table.*, category_table.categoryName FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID LEFT JOIN category_table ON task_table.categoryId = category_table.categoryId"
            + " WHERE task_table.mNotificationStartMillis >= :startMillis AND task_table.mNotificationStartMillis < :endMillis AND task_table.mName LIKE :nameRegex")
    public abstract LiveData<List<TaskData>> getTasksWithThemeByNotificationByName(long startMillis, long endMillis, String nameRegex);

    @TestOnly
    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID"
            + " WHERE task_table.mNotificationStartMillis >= :startMillis AND task_table.mNotificationStartMillis < :endMillis")
    public abstract List<TaskData> getTasksLiveDataWithThemeTEST(long startMillis, long endMillis);

    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID WHERE task_table.task_ID = :taskID")
    public abstract LiveData<TaskData> getTaskThemeLiveData(String taskID);

    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID WHERE task_table.categoryId = :categoryID")
    public abstract LiveData<List<TaskData>> getTasksWithThemeByNotification(String categoryID);

    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID WHERE task_table.categoryId = :categoryID AND task_table.mParentID = ''")
    public abstract LiveData<List<TaskData>> getSingleTasksWithThemeLiveData(String categoryID);

    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID WHERE task_table.categoryId = :categoryID AND task_table.mParentID != '' ORDER BY task_table.timeCreated DESC")
    public abstract LiveData<List<TaskData>> getProjectTasksWithThemeLiveData(String categoryID);

    @TestOnly
    @Query("SELECT task_table.*, theme_table.* FROM task_table LEFT JOIN theme_table ON task_table.mThemeID = theme_table.theme_ID WHERE task_table.categoryId = :catID")
    public abstract List<TaskData> getTasksLiveDataWithThemeTEST(long catID);

    @Query("SELECT * FROM task_table WHERE task_ID = :id")
    public abstract LiveData<Task> getTaskLiveData(String id);
    @Query("SELECT * FROM task_table WHERE task_ID = :id")
    public abstract Task getTask(String id);

    @Query("SELECT * FROM task_table WHERE (task_table.mParentID IS NULL OR task_table.mParentID = '') AND task_table.categoryId = :categoryID ORDER BY mName ASC")
    public abstract LiveData<List<TaskAndTheme>> getSingleTaskAndThemeByCategory(String categoryID);

    @Query("SELECT * FROM task_table WHERE task_table.mParentID IS NULL")
    public abstract List<TaskAndTheme> getSingleTaskAndThemeByCategoryTEST();

    @Query("SELECT * FROM task_table WHERE (task_table.mParentID IS NULL OR task_table.mParentID = '') AND task_table.categoryId = :categoryID AND task_table.mName LIKE :nameRegex")
    public abstract LiveData<List<TaskAndTheme>> getSingleTaskAndThemeByCategoryByName(String categoryID, String nameRegex);

    @Query("SELECT * FROM task_table"
            + " WHERE task_table.startTime > 0 AND NOT task_table.notificationEnabled AND task_table.startTime <= :startMillis AND task_table.endTime >= :startMillis AND task_table.generalNotificationEnabled > 0"
            + " ORDER BY task_table.mImportance ASC")
    public abstract List<TaskNotificationData> getTasksNotificationData(long startMillis);

    @Query("SELECT * FROM task_table WHERE (task_table.syncing = 1)")
    public abstract LiveData<List<Task>> getAllSyncing();

    @Insert
    public abstract long insertProject(Project obj);

    @Transaction
    public int decomposeTask(String id, Project project){
        int res1 = deleteById(id);
        long res2 = insertProject(project);
        if(res1 > -1 && res2 > -1){
            return 0;
        }
        else{
            return -1;
        }
    }
}
