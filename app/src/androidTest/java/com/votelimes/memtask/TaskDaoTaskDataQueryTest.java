package com.votelimes.memtask;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.votelimes.memtask.dao.CategoryDao;
import com.votelimes.memtask.dao.TaskDao;
import com.votelimes.memtask.dao.ThemeDao;
import com.votelimes.memtask.model.Category;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskData;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.storageutils.Database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTaskDataQueryTest {
    private TaskDao taskDao;
    private ThemeDao themeDao;
    private CategoryDao categoryDao;

    private Database db;
    private final long categoryID = 1;
    private int tasksCount = 200;

    @Before
    public void createAndPopulateDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, Database.class).build();
        taskDao = db.taskDao();
        themeDao = db.themeDao();
        categoryDao = db.categoryDao();

        //categoryDao.insert(new Category());

        populateDBWithTasks(tasksCount, new Task("", "", categoryID));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void queryCorrectnessTesting() throws Exception {
        List<TaskData> byCategory = taskDao.getTasksLiveDataWithThemeTEST(categoryID);
        List<Category> categories = categoryDao.getCategories();

        assertThat(byCategory.size(), equalTo(tasksCount));
        assertThat(byCategory.get(byCategory.size()-1).theme, equalTo(null));
    }

    @Test(timeout = 200)
    public void estimateReadTasksTime() throws Exception {
        List<TaskData> byCategory = taskDao.getTasksLiveDataWithThemeTEST(categoryID);
    }

    private void populateDBWithTasks(int count, Task task){
        for(int i = 0; i < count; i++){
            Theme theme = new Theme("TEST_THEME", "#213123", "#213123", "#213123", 1);
            themeDao.insert(theme);
            task.setThemeID(theme.getID());
            taskDao.insert(task);
            task.reGenerateUUID();
        }
        task.setThemeID("");
        taskDao.insert(task);
        tasksCount++;

        task.setCategoryId(-1);
        task.reGenerateUUID();
        taskDao.insert(task);

        task.reGenerateUUID();
        taskDao.insert(task);
        task.reGenerateUUID();
    }
}
