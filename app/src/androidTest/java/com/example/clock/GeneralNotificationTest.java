package com.example.clock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.clock.app.App;
import com.example.clock.dao.CategoryDao;
import com.example.clock.dao.ProjectDao;
import com.example.clock.dao.TaskDao;
import com.example.clock.dao.ThemeDao;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.ProjectData;
import com.example.clock.model.Task;
import com.example.clock.model.TaskNotificationData;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GeneralNotificationTest {
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
    public void notificationQueryAlgorithmTest(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime rangeStart = LocalDateTime.parse("01.03.2022 00:00", dtf);
        LocalDateTime rangeEnd = LocalDateTime.parse("01.04.2022 00:00", dtf);
        List<TaskNotificationData> data = taskDao.getTasksNotificationData(rangeStart.toEpochSecond(ZoneOffset.UTC)*1000, rangeEnd.toEpochSecond(ZoneOffset.UTC)*1000);


    }

    @Test
    public void queryTest(){
        List<TaskNotificationData> data = taskDao.getTasksNotificationData(0L, 1546602923713L);
        List<Task> allData = taskDao.getAll();

        data.forEach(item -> {
            if(item.project != null){
                Log.d("GeneralNotificationTest", "queryTest: PROJECT NAME EXISTS");
                assertNotNull(item.project);
            }
            if(item.task.getName() == null){
                assertNotNull(item.task.getName());
                allData.forEach(innerItem -> {
                    if(item.task.getTaskId().equals(innerItem.getTaskId())){
                        Log.d("GeneralNotificationTest", "queryTest: FIND NULL NAME DUPLICATE");
                        Log.d("GeneralNotificationTest", item.task.getTaskId());
                    }
                });
            }
        });

        assertNotEquals(data.size(), 0);
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
        defaultProjectsList.get(0).setRange("24.03.2022", "28.03.2022");
        defaultProjectsList.get(0).setThemeID(defaultThemesList.get(21).getID());
        project1ID = defaultProjectsList.get(0).getProjectId();

        defaultProjectsList.add(new Project("Сделать презентацию", "Способы оптимизации алгоритмов", 2));
        defaultProjectsList.get(1).setRange("01.03.2022", "15.03.2022");
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
            calendar.setTime(sdf.parse("25.03.2022 00:00:00"));
        } catch (ParseException e){
            Log.e("INITIAL SETUP ERROR: ", e.getMessage());
        }

        defaultTasksList.add(new Task("Оплатить счета", "Оплатить счета за дом", 1 ));
        defaultTasksList.get(0).setAlarmTime("25.03.2022 11:20");
        defaultTasksList.get(0).setRange("20.03.2022", "25.03.2022");
        defaultTasksList.get(0).setNotificationEnabled(true);
        defaultTasksList.get(0).setRepeatMode(4);
        defaultTasksList.get(0).setImportance(0);
        defaultTasksList.get(0).setThemeID(defaultThemesList.get(20).getID());

        defaultTasksList.add(new Task("Полить цветы", "Полить все цветы кроме, замиокулькаса", 1 ));
        defaultTasksList.get(1).setAlarmTime("25.03.2022 17:40");
        defaultTasksList.get(1).setNotificationEnabled(true);
        defaultTasksList.get(1).setRepeatMode(3);
        defaultTasksList.get(1).setTuesday(true);
        defaultTasksList.get(1).setThemeID(defaultThemesList.get(0).getID());

        defaultTasksList.add(new Task("Забрать посылку", "", 1 ));
        defaultTasksList.get(2).setAlarmTime("14.03.2022 16:00");
        defaultTasksList.get(2).setRange("14.03.2022", "17.03.2022");
        defaultTasksList.get(2).setNotificationEnabled(true);
        defaultTasksList.get(2).setThemeID(defaultThemesList.get(1).getID());
        defaultTasksList.get(2).setImportance(1);

        defaultTasksList.add(new Task("Утренняя разминка", "", 4 ));
        defaultTasksList.get(3).setAlarmTime("26.03.2022 10:00");
        defaultTasksList.get(3).setNotificationEnabled(true);
        defaultTasksList.get(3).setRepeatMode(3);
        defaultTasksList.get(3).setMonday(true);
        defaultTasksList.get(3).setWednesday(true);
        defaultTasksList.get(3).setFriday(true);
        defaultTasksList.get(3).setThemeID(defaultThemesList.get(2).getID());

        defaultTasksList.add(new Task("Забрать ключи", "Ключи от офиса 303", 2 ));
        defaultTasksList.get(4).setAlarmTime("26.03.2022 15:00");
        defaultTasksList.get(4).setRange("26.03.2022", "26.03.2022");
        defaultTasksList.get(4).setNotificationEnabled(true);
        defaultTasksList.get(4).setThemeID(defaultThemesList.get(0).getID());

        defaultTasksList.add(new Task("Отправиться на прием к врачу", "Кабинет 6", 4 ));
        defaultTasksList.get(5).setAlarmTime("11.03.2022 07:40");
        defaultTasksList.get(5).setNotificationEnabled(true);
        defaultTasksList.get(5).setRepeatMode(1);
        defaultTasksList.get(5).setThemeID(defaultThemesList.get(1).getID());

        // Project tasks
        defaultTasksList.add(new Task("Поискать номер регистратуры", "", 4 ));
        defaultTasksList.get(6).setParentID(defaultProjectsList.get(0).getProjectId());
        defaultTasksList.get(6).setThemeID(defaultThemesList.get(20).getID());
        defaultTasksList.get(6).setRange("05.03.2022", "10.03.2022");

        defaultTasksList.add(new Task("Позвонить по номеру", "", 4 ));
        defaultTasksList.get(7).setParentID(defaultProjectsList.get(0).getProjectId());
        defaultTasksList.get(7).setThemeID(defaultThemesList.get(20).getID());
        defaultTasksList.get(7).setRange("11.03.2022", "13.03.2022");

        defaultTasksList.add(new Task("Записать дату приема", "", 4));
        defaultTasksList.get(8).setParentID(defaultProjectsList.get(0).getProjectId());
        defaultTasksList.get(8).setThemeID(defaultThemesList.get(20).getID());
        defaultTasksList.get(8).setRange("13.03.2022", "15.03.2022");


        // Project tasks
        defaultTasksList.add(new Task("Подготовить литературу", "Поискать на programmer-lib", 2 ));
        defaultTasksList.get(9).setAlarmTime("24.03.2022 11:00");
        defaultTasksList.get(9).setRange("24.03.2022", "25.03.2022");
        defaultTasksList.get(9).setNotificationEnabled(true);
        defaultTasksList.get(9).setParentID(defaultProjectsList.get(1).getProjectId());
        defaultTasksList.get(9).setThemeID(defaultThemesList.get(20).getID());

        defaultTasksList.add(new Task("Определить структуру", "3 раздела, 12 слайдов", 2 ));
        defaultTasksList.get(10).setAlarmTime("25.03.2022 11:00");
        defaultTasksList.get(10).setRange("25.03.2022", "26.03.2022");
        defaultTasksList.get(10).setNotificationEnabled(true);
        defaultTasksList.get(10).setParentID(defaultProjectsList.get(1).getProjectId());
        defaultTasksList.get(10).setImportance(0);
        defaultTasksList.get(10).setThemeID(defaultThemesList.get(12).getID());

        defaultTasksList.add(new Task("Написать текст", "8 страниц, 14пт", 2 ));
        defaultTasksList.get(11).setAlarmTime("27.03.2022 17:00");
        defaultTasksList.get(11).setRange("27.03.2022", "31.03.2022");
        defaultTasksList.get(11).setNotificationEnabled(true);
        defaultTasksList.get(11).setParentID(defaultProjectsList.get(1).getProjectId());
        defaultTasksList.get(11).setThemeID(defaultThemesList.get(11).getID());

        defaultTasksList.add(new Task("Проверить новую версию Room", "Последняя 2.4.1", 2 ));
        defaultTasksList.get(12).setAlarmTime("20.03.2022 17:00");
        defaultTasksList.get(12).setRange("20.03.2022", "25.03.2022");
        defaultTasksList.get(12).setNotificationEnabled(true);
        defaultTasksList.get(12).setThemeID(defaultThemesList.get(10).getID());

        defaultTasksList.add(new Task("Посмотреть видео про коптеры", "Любое", 3 ));
        defaultTasksList.get(13).setThemeID(defaultThemesList.get(15).getID());
        defaultTasksList.get(13).setRange("01.03.2022", "31.03.2022");

        for (Task task: defaultTasksList) {
            taskDao.insert(task);
        }
    }
}
