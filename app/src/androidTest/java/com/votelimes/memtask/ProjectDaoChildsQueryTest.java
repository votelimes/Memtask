package com.votelimes.memtask;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.votelimes.memtask.app.App;
import com.votelimes.memtask.dao.CategoryDao;
import com.votelimes.memtask.dao.ProjectDao;
import com.votelimes.memtask.dao.TaskDao;
import com.votelimes.memtask.dao.ThemeDao;
import com.votelimes.memtask.model.Category;
import com.votelimes.memtask.model.Project;
import com.votelimes.memtask.model.ProjectData;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.storageutils.Database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ProjectDaoChildsQueryTest {
    private TaskDao taskDao;
    private ProjectDao projectDao;
    private CategoryDao categoryDao;
    private ThemeDao themeDao;

    private String project1ID;
    private String project2ID;

    private Database db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, Database.class).build();
        taskDao = db.taskDao();
        projectDao = db.projectDao();
        categoryDao = db.categoryDao();
        themeDao = db.themeDao();

        populateDB();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void projectsWithThemesByCategoryTest(){
        List<ProjectData> projectsSet = projectDao.getProjectsDataByCatTEST(4);

        assertThat(projectsSet.size(), equalTo(1));
        assertNotNull(projectsSet.get(0).theme);
        assertNotNull(projectsSet.get(0).tasksData.get(0).theme);
    }

    @Test
    public void allProjectsWithThemesTest(){
        List<ProjectData> allProjects = projectDao.getProjectsDataTEST2();


        assertThat(allProjects.size(), equalTo(2));
        assertNotNull(allProjects.get(0).theme);
        assertNotNull(allProjects.get(0).tasksData.get(0).theme);
    }

    @Test
    public void allProjectsTest() throws Exception {
        List<ProjectData> allProjects = projectDao.getProjectsDataTEST();

        assertNotNull(allProjects.get(0).theme);
    }

    @Test
    public void categoryProjectsTest() throws Exception {
        List<ProjectData> allProjects = projectDao.getProjectsDataTEST();
        List<ProjectData> projectsWithCategory = projectDao.getProjectsDataByCatTEST(2);

        assertNotNull(projectsWithCategory.get(0).theme);
        assertThat(projectsWithCategory.get(0).tasksData.size(), equalTo(allProjects.get(1).tasksData.size()));
    }

    private void populateDB(){
        // Themes installation

        List<Theme> defaultThemesList = new ArrayList<Theme>(20);

        defaultThemesList.add(new Theme("Rajah",
                "#FFB563", "#68B0AB", 0));
        defaultThemesList.get(0).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(0).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("Celadon",
                "#BAF2BB", "#F47B93", 0));
        defaultThemesList.get(1).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(1).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("Fiery Rose",
                "#EF626C", "#607196", 0));
        defaultThemesList.get(2).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(2).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(2).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Orange Red Crayola",
                "#FF5E5B", "#50B2C0", 0));
        defaultThemesList.get(3).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(3).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(3).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Middle Green",
                "#5B8C5A", "#311E10", 0));
        defaultThemesList.get(4).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(4).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(4).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Sky Blue Crayola",
                "#90E0F3", "#311E10", 0));
        defaultThemesList.get(5).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(5).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("Orange Yellow",
                "#FABC2A", "#662C91", 0));
        defaultThemesList.get(6).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(6).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("Mauve Taupe",
                "#925E78", "#F15152", 0));
        defaultThemesList.get(7).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(4).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(7).setIconColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Cyan Process",
                "#00A5E0", "#464D77", 0));
        defaultThemesList.get(8).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(8).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(8).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Orchid Crayola",
                "#EF9CDA", "#568259", 0));
        defaultThemesList.get(9).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(9).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("Black Coffee",
                "#32292F", "#FF715B", 0));
        defaultThemesList.get(10).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(10).setIconColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(10).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Umber",
                "#705D56", "#58BC82", 0));
        defaultThemesList.get(11).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(11).setIconColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Ocean Green",
                "#65B891", "#FF674D", 0));
        defaultThemesList.get(12).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(12).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(12).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Pastel Pink",
                "#DAA89B", "#F15152", 0));
        defaultThemesList.get(13).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(13).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("Blue Bell",
                "#998FC7", "#F15152", 0));
        defaultThemesList.get(14).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(14).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(14).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Steel Teal",
                "#6E8387", "#F15152", 0));
        defaultThemesList.get(15).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(15).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(15).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Caput Mortuum",
                "#522A27", "#B96D40", 0));
        defaultThemesList.get(16).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(16).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(16).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Satin Sheen Gold",
                "#C59849", "#549F93", 0));
        defaultThemesList.get(17).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(17).setIconColor(App.getInstance().getColor(R.color.act_text_grey));
        defaultThemesList.get(17).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));


        defaultThemesList.add(new Theme("United Nations Blue",
                "#6290C3", "#549F93", 0));
        defaultThemesList.get(18).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(18).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(18).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("Mandarin",
                "#F37748", "#549F93", 0));
        defaultThemesList.get(19).setMainTextColor(App.getInstance().getColor(R.color.act_text_light));
        defaultThemesList.get(19).setIconColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(19).setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_light));

        defaultThemesList.add(new Theme("MainTaskTheme",
                "#F7EDE2", "#F15152", 0));
        defaultThemesList.get(20).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(20).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        defaultThemesList.add(new Theme("MainProjectTheme",
                "#F7EDE2", "#F15152", 0));
        defaultThemesList.get(21).setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
        defaultThemesList.get(21).setIconColor(App.getInstance().getColor(R.color.act_text_main));

        for (Theme theme: defaultThemesList) {
            if(theme.getAdditionalTextColor() == -1){
                theme.setAdditionalTextColor(App.getInstance().getColor(R.color.act_text_grey));
            }

            theme.setBaseTheme(true);
            themeDao.insert(theme);
        }

        // Categories installation
        List<Category> defaultCategoriesList = new ArrayList<Category>(4);
        defaultCategoriesList.add(new Category("Дом",
                "Домашние дела, покупки и т.д.", ""));
        defaultCategoriesList.get(0).setThemeID(defaultThemesList.get(12).getID());

        defaultCategoriesList.add(new Category("Работа",
                "Будильники, встречи и т.д.", ""));
        defaultCategoriesList.get(1).setThemeID(defaultThemesList.get(13).getID());

        defaultCategoriesList.add(new Category("Прочее",
                "Все остальное", ""));
        defaultCategoriesList.get(2).setThemeID(defaultThemesList.get(9).getID());

        defaultCategoriesList.add(new Category("Спорт / Здоровье",
                "", ""));
        defaultCategoriesList.get(3).setThemeID(defaultThemesList.get(11).getID());

        for (Category category: defaultCategoriesList) {
            categoryDao.insert(category);
        }

        // Some projects installation
        List<Project> defaultProjectsList = new ArrayList<Project>(5);
        defaultProjectsList.add(new Project("Вылечить зуб", "", 4));
        defaultProjectsList.get(0).setRange("24.02.2022", "28.02.2022");
        defaultProjectsList.get(0).setThemeID(defaultThemesList.get(21).getID());
        project1ID = defaultProjectsList.get(0).getProjectId();

        defaultProjectsList.add(new Project("Сделать презентацию", "Способы оптимизации алгоритмов", 2));
        defaultProjectsList.get(1).setRange("1.02.2022", "15.02.2022");
        defaultProjectsList.get(1).setThemeID(defaultThemesList.get(21).getID());
        project2ID = defaultProjectsList.get(1).getProjectId();

        for (Project project: defaultProjectsList) {
            projectDao.insert(project);
        }

        // Some Tasks installation
        List<Task> defaultTasksList = new ArrayList<Task>(5);
        Calendar calendar = GregorianCalendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            calendar.setTime(sdf.parse("25.02.2022 00:00:00"));
        } catch (ParseException e){
            Log.e("INITIAL SETUP ERROR: ", e.getMessage());
        }

        defaultTasksList.add(new Task("Оплатить счета", "Оплатить счета за дом", 1 ));
        defaultTasksList.get(0).setAlarmTime("25.02.2022 11:20");
        defaultTasksList.get(0).setNotificationEnabled(true);
        defaultTasksList.get(0).setRepeatMode(4);
        defaultTasksList.get(0).setImportance(0);
        defaultTasksList.get(0).setThemeID(defaultThemesList.get(20).getID());

        defaultTasksList.add(new Task("Полить цветы", "Полить все цветы кроме, замиокулькаса", 1 ));
        defaultTasksList.get(1).setAlarmTime("25.02.2022 17:40");
        defaultTasksList.get(1).setNotificationEnabled(true);
        defaultTasksList.get(1).setRepeatMode(3);
        defaultTasksList.get(1).setTuesday(true);
        defaultTasksList.get(1).setThemeID(defaultThemesList.get(0).getID());

        defaultTasksList.add(new Task("Забрать посылку", "", 1 ));
        defaultTasksList.get(2).setAlarmTime("26.02.2022 16:00");
        defaultTasksList.get(2).setNotificationEnabled(true);
        defaultTasksList.get(2).setThemeID(defaultThemesList.get(1).getID());
        defaultTasksList.get(2).setImportance(1);

        defaultTasksList.add(new Task("Утренняя разминка", "", 4 ));
        defaultTasksList.get(3).setAlarmTime("26.02.2022 10:00");
        defaultTasksList.get(3).setNotificationEnabled(true);
        defaultTasksList.get(3).setRepeatMode(3);
        defaultTasksList.get(3).setMonday(true);
        defaultTasksList.get(3).setWednesday(true);
        defaultTasksList.get(3).setFriday(true);
        defaultTasksList.get(3).setThemeID(defaultThemesList.get(2).getID());

        defaultTasksList.add(new Task("Забрать ключи", "Ключи от офиса 303", 2 ));
        defaultTasksList.get(4).setAlarmTime("26.02.2022 15:00");
        defaultTasksList.get(4).setNotificationEnabled(true);
        defaultTasksList.get(4).setThemeID(defaultThemesList.get(0).getID());

        defaultTasksList.add(new Task("Отправиться на прием к врачу", "Кабинет 6", 4 ));
        defaultTasksList.get(5).setAlarmTime("11.02.2022 7:40");
        defaultTasksList.get(5).setNotificationEnabled(true);
        defaultTasksList.get(5).setRepeatMode(1);
        defaultTasksList.get(5).setThemeID(defaultThemesList.get(1).getID());

        // Project tasks
        defaultTasksList.add(new Task("Поискать номер регистратуры", "", 4 ));
        defaultTasksList.get(6).setParentID(defaultProjectsList.get(0).getProjectId());
        defaultTasksList.get(6).setThemeID(defaultThemesList.get(20).getID());

        defaultTasksList.add(new Task("Позвонить по номеру", "", 4 ));
        defaultTasksList.get(7).setParentID(defaultProjectsList.get(0).getProjectId());
        defaultTasksList.get(7).setThemeID(defaultThemesList.get(20).getID());

        defaultTasksList.add(new Task("Записать дату приема", "", 4));
        defaultTasksList.get(8).setParentID(defaultProjectsList.get(0).getProjectId());
        defaultTasksList.get(8).setThemeID(defaultThemesList.get(20).getID());


        // Project tasks
        defaultTasksList.add(new Task("Подготовить литературу", "Поискать на programmer-lib", 2 ));
        defaultTasksList.get(9).setAlarmTime("24.02.2022 11:00");
        defaultTasksList.get(9).setNotificationEnabled(true);
        defaultTasksList.get(9).setParentID(defaultProjectsList.get(1).getProjectId());
        defaultTasksList.get(9).setThemeID(defaultThemesList.get(20).getID());

        defaultTasksList.add(new Task("Определить структуру", "3 раздела, 12 слайдов", 2 ));
        defaultTasksList.get(10).setAlarmTime("25.02.2022 11:00");
        defaultTasksList.get(10).setNotificationEnabled(true);
        defaultTasksList.get(10).setParentID(defaultProjectsList.get(1).getProjectId());
        defaultTasksList.get(10).setImportance(0);
        defaultTasksList.get(10).setThemeID(defaultThemesList.get(12).getID());

        defaultTasksList.add(new Task("Написать текст", "8 страниц, 14пт", 2 ));
        defaultTasksList.get(11).setAlarmTime("27.02.2022 17:00");
        defaultTasksList.get(11).setNotificationEnabled(true);
        defaultTasksList.get(11).setParentID(defaultProjectsList.get(1).getProjectId());
        defaultTasksList.get(11).setThemeID(defaultThemesList.get(11).getID());

        defaultTasksList.add(new Task("Проверить новую версию Room", "Последняя 2.4.1", 2 ));
        defaultTasksList.get(12).setAlarmTime("20.02.2022 17:00");
        defaultTasksList.get(12).setNotificationEnabled(true);
        defaultTasksList.get(12).setThemeID(defaultThemesList.get(10).getID());

        defaultTasksList.add(new Task("Посмотреть видео про коптеры", "Любое", 3 ));
        defaultTasksList.get(13).setThemeID(defaultThemesList.get(15).getID());

        for (Task task: defaultTasksList) {
            taskDao.insert(task);
        }
    }
}
