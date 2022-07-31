package com.votelimes.memtask;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.votelimes.memtask.dao.TaskDao;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskData;
import com.votelimes.memtask.storageutils.Database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TaskDaoMainWriteReadTest {
    private TaskDao taskDao;
    private Database db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, Database.class).build();
        taskDao = db.taskDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeTaskAndReadInList() throws Exception {
        Task task = new Task("TaskName", "", -1);
        task.setNotificationStartMillis(500);

        populateDB(5, task);

        List<TaskData> byName = taskDao.getTasksLiveDataWithThemeTEST(0, 501);
        assertThat(byName.get(0).task.getTaskId(), equalTo(task.getTaskId()));

    }

    @Test
    public void writeTaskQueryCorrectnessTesting() throws Exception {
        Task task = new Task("TaskName", "", -1);
        task.setNotificationStartMillis(900);
        int totalElements = 0;
        populateDBNotEqual(10, task); totalElements += 10;
        task.setNotificationStartMillis(901);
        populateDBNotEqual(10, task);

        List<TaskData> byName = taskDao.getTasksLiveDataWithThemeTEST(0, 901);
        assertThat(byName.size(), equalTo(totalElements));
    }

    @Test(timeout = 300)
    public void estimateWriteTasksTime() throws Exception {
        Task task = new Task("TaskName", "", -1);
        task.setNotificationStartMillis(500);
        populateDBNotEqual(100, task);
    }
    @Test(timeout = 200)
    public void estimateReadTasksTime() throws Exception {
        Task task = new Task("TaskName", "", -1);
        task.setNotificationStartMillis(500);
        List<TaskData> byName = taskDao.getTasksLiveDataWithThemeTEST(0, 901);
    }

    private void populateDB(int count, Task task){
        String taskID = task.getTaskId();
        for(int i = 0; i < count; i++){
            taskDao.insert(task);
            task.reGenerateUUID();
        }
        task.setTaskId(taskID);
    }
    private void populateDBNotEqual(int count, Task task){
        for(int i = 0; i < count; i++){
            task.reGenerateUUID();
            taskDao.insert(task);
        }
    }
}
