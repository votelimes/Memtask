package com.votelimes.memtask;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskNotificationData;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.storageutils.Database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

        populateDB4();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void notificationQueryAlgorithmTest(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime today = LocalDateTime.parse("16.03.2022 00:00", dtf);

        List<TaskNotificationData> activeTasks = taskDao.getTasksNotificationData(today.toEpochSecond(ZoneOffset.UTC)*1000);
        List<DayLoad> days;
        LocalDateTime startInterval;
        LocalDateTime endInterval;

        if(today.getHour() < 10){
            startInterval = today;
        }
        else{
            startInterval = today.plusDays(1);
        }
        endInterval = startInterval;
        for (int i = 0; i < activeTasks.size(); i++){
            if(activeTasks.get(i).task.getEndTime() > (endInterval.toEpochSecond(ZoneOffset.UTC) * 1000)){
                endInterval = LocalDateTime.ofEpochSecond(activeTasks.get(i).task.getEndTime() / 1000, 0, ZoneOffset.UTC);
            }
        }

        days = new ArrayList<DayLoad>((int) ChronoUnit.DAYS.between(startInterval, endInterval));
        for (int i = 0; i < (int) ChronoUnit.DAYS.between(startInterval, endInterval) + 1; i++){
            days.add(new DayLoad(today.toLocalDate().plusDays(i)));
        }

        // Расчет нагрузки
        for(int i = 0; i < activeTasks.size(); i++){
            Task task = activeTasks.get(i).task;
            LocalDateTime now = today;
            LocalDateTime start = LocalDateTime.ofEpochSecond(task.getStartTime()/1000, 0, ZoneOffset.UTC);
            LocalDateTime end = LocalDateTime.ofEpochSecond(task.getEndTime()/1000, 0, ZoneOffset.UTC);

            int dayStartIndex = (int) ChronoUnit.DAYS.between(today, start);
            if(start.toLocalDate().isBefore(today.toLocalDate())){
                dayStartIndex = 0;
            }
            int dayEndIndex = (int) ChronoUnit.DAYS.between(now, end);

            int stepsMax = (task.getDuration() - task.getProgress());
            int stepNow = dayStartIndex;
            int stepSize = (int) task.getStep();
            //long daysRange = ChronoUnit.DAYS.between(now, end);

            while(stepNow < stepsMax){
                if(stepNow >= days.size()){
                    break;
                }
                if(stepNow + stepSize >= stepsMax){
                    stepSize = stepsMax - stepNow;
                }
                if(stepNow == 0 && dayEndIndex / 2 > stepsMax){
                    stepNow += stepSize;
                    stepsMax += stepSize;
                    continue;
                }
                boolean isAdded = days.get(stepNow).addOnTop(stepSize, false);
                if(!isAdded){
                    int currentDay = stepNow;
                    boolean outOfRange = false;

                    // Пустые дни вправо
                    while(!isAdded){
                        if(currentDay > dayEndIndex){
                            outOfRange = true;
                            break;
                        }
                        isAdded = days.get(currentDay).addOnTop(stepSize, false);
                        if(isAdded){
                            outOfRange = false;
                            break;
                        }
                        currentDay++;
                    }
                    // Пустые дни влево
                    if(outOfRange || !isAdded){
                        currentDay = stepNow;
                        while(!isAdded){
                            if(currentDay < dayStartIndex){
                                outOfRange = true;
                                break;
                            }
                            isAdded = days.get(currentDay).addOnTop(stepSize, false);
                            if(isAdded){
                                outOfRange = false;
                                break;
                            }
                            currentDay--;
                        }
                    }

                    // Любые дни вправо
                    if(outOfRange || !isAdded){
                        currentDay = stepNow;
                        outOfRange = false;
                        while(!isAdded){
                            if(currentDay > dayEndIndex){
                                outOfRange = true;
                                break;
                            }
                            isAdded = days.get(currentDay).addOnTop(stepSize, true);
                            if(isAdded){
                                outOfRange = false;
                                break;
                            }
                            currentDay++;
                        }
                    }
                    // Любые дни влево
                    if(outOfRange || !isAdded){
                        currentDay = stepNow;
                        while(!isAdded){
                            if(currentDay < dayStartIndex){
                                outOfRange = true;
                                break;
                            }
                            isAdded = days.get(currentDay).addOnTop(stepSize, true);
                            if(isAdded){
                                outOfRange = false;
                                break;
                            }
                            currentDay--;
                        }
                    }
                }

                if(stepNow == 0){
                    long notificationMillis = days.get(stepNow).getLastIntervalMillis(stepSize);
                }
                stepNow+=stepSize;
            }
        }

        for (int i = 0; i < days.size(); i++){
            Log.d("Day "+String.valueOf(i+1), "Loaded: "+String.valueOf(!(days.get(i).hour[9]==0)));
        }
        assertNotNull(days);
    }

    private boolean beforeOrEqual(LocalDateTime objectStart, LocalDateTime contentStart){
        return objectStart.isBefore(contentStart) || objectStart.toLocalDate().isEqual(contentStart.toLocalDate());
    }

    @Test
    public void queryTest(){
        List<TaskNotificationData> data = taskDao.getTasksNotificationData(1546602923713L);
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

    private void populateDB2(){
        List<Task> defaultTasksList = new ArrayList<Task>(5);

        defaultTasksList.add(new Task("Задача 1", "", 1 ));
        defaultTasksList.get(0).setAlarmTime("25.03.2022 11:20");
        defaultTasksList.get(0).setNotificationEnabled(true);
        defaultTasksList.get(0).setRange("16.03.2022", "18.03.2022");
        defaultTasksList.get(0).setDuration(4);
        defaultTasksList.get(0).setImportance(2);

        defaultTasksList.add(new Task("Задача 2", "", 1 ));
        defaultTasksList.get(1).setRange("16.03.2022", "16.03.2022");
        defaultTasksList.get(1).setDuration(4);
        defaultTasksList.get(1).setImportance(2);

        defaultTasksList.add(new Task("Задача 3", "", 1 ));
        defaultTasksList.get(2).setRange("18.03.2022", "19.03.2022");
        defaultTasksList.get(2).setDuration(4);
        defaultTasksList.get(2).setImportance(1);

        defaultTasksList.add(new Task("Задача 4", "", 1 ));
        defaultTasksList.get(3).setRange("16.03.2022", "18.03.2022");
        defaultTasksList.get(3).setImportance(0);

        defaultTasksList.add(new Task("Задача 5", "", 1 ));
        defaultTasksList.get(4).setRange("16.03.2022", "25.03.2022");
        defaultTasksList.get(4).setDuration(4);
        defaultTasksList.get(4).setImportance(2);

        defaultTasksList.add(new Task("Задача 6", "", 1 ));
        defaultTasksList.get(5).setRange("14.03.2022", "25.03.2022");
        defaultTasksList.get(5).setDuration(4);
        defaultTasksList.get(5).setImportance(3);

        defaultTasksList.add(new Task("Задача 7", "", 1 ));
        defaultTasksList.get(6).setRange("16.03.2022", "23.03.2022");
        defaultTasksList.get(6).setImportance(0);

        defaultTasksList.add(new Task("Задача 8", "", 1 ));
        defaultTasksList.get(7).setRange("16.03.2022", "20.03.2022");

        for (Task task: defaultTasksList) {
            taskDao.insert(task);
        }
    }

    private void populateDB3(){
        List<Task> defaultTasksList = new ArrayList<Task>(5);

        defaultTasksList.add(new Task("Задача 1", "", 1 ));
        defaultTasksList.get(0).setRange("16.03.2022", "24.03.2022");
        defaultTasksList.get(0).setDuration(8);
        defaultTasksList.get(0).setImportance(2);

        defaultTasksList.add(new Task("Задача 2", "", 1 ));
        defaultTasksList.get(1).setRange("16.03.2022", "24.03.2022");
        defaultTasksList.get(1).setDuration(2);
        defaultTasksList.get(1).setImportance(2);

        for (Task task: defaultTasksList) {
            taskDao.insert(task);
        }
    }

    private void populateDB4(){
        List<Task> defaultTasksList = new ArrayList<Task>(5);

        defaultTasksList.add(new Task("Задача 1", "", 1 ));
        defaultTasksList.get(0).setRange("16.03.2022", "24.03.2022");
        defaultTasksList.get(0).setDuration(3);
        defaultTasksList.get(0).setImportance(2);

        defaultTasksList.add(new Task("Задача 2", "", 1 ));
        defaultTasksList.get(1).setRange("16.03.2022", "18.03.2022");
        defaultTasksList.get(1).setDuration(2);
        defaultTasksList.get(1).setImportance(2);

        for (Task task: defaultTasksList) {
            taskDao.insert(task);
        }
    }

    private class DayLoad {
        public int hour[] = new int[24];

        private final int START_WORK_DAY_HOUR = 9;
        private final int END_WORK_DAY_HOUR = 22;
        private LocalDate date;

        public DayLoad(LocalDate date){
            this.date = date;
        }

        public boolean addLoad(int startHour, int count){
            int index = startHour;
            int current = 0;

            while(current < count){
                hour[index]++;
                index++;
                current++;
                if(index >= END_WORK_DAY_HOUR){
                    return false;
                }
            }
            return true;
        }
        public boolean addOnTop(int count, boolean hardInsert){
            int i = START_WORK_DAY_HOUR;
            if(hour[START_WORK_DAY_HOUR] != 0 && !hardInsert){
                return false;
            }
            while (hour[i] != 0) {
                if (i >= END_WORK_DAY_HOUR) {
                    return false;
                }
                i++;
            }
            return addLoad(i, count);
        }
        public boolean isBefore(LocalDate date){
            return this.date.isBefore(date);
        }
        public boolean isAfter(LocalDate date){
            return this.date.isAfter(date);
        }

        public int getLastIntervalHour(int count){
            int c = 0;
            int hourOfDay = 0;
            for(int i = 23; i > -1; i--){
                if(hour[i] != 0 && c < count){
                    hourOfDay = i + 1;
                    c++;
                }
            }
            return hourOfDay;
        }
        public long getLastIntervalMillis(int count){
            return date.atStartOfDay().withHour(getLastIntervalHour(count)).toEpochSecond(ZoneOffset.UTC) * 1000;
        }
    }
}
