package com.example.clock.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.fragments.CalendarFragment;
import com.example.clock.fragments.CardsListFragment;
import com.example.clock.fragments.CategoriesListFragment;
import com.example.clock.fragments.SettingsFragment;
import com.example.clock.fragments.StatisticFragment;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.example.clock.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //ViewModel and binding
    MainViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityMainBinding mActivityBinding;


    //Menu
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        mFactory = new ViewModelFactoryBase(getApplication(), App.getDatabase(), App.getSilentDatabase());
        mViewModel = new ViewModelProvider(this, mFactory).get(MainViewModel.class);
        mActivityBinding.setViewmodel(mViewModel);

        //AppBarLayout appTopLayout = ()findViewById(R.id.main_app_bar);
        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.open();
        });

        setupApplication();

        Fragment nextFragment = null;
        String title = "";
        if(App.getSettings().getLastCategory().first != -1) {
            switch ((int) App.getSettings().getCurrentWindow()) {
                // Categories list
                case 1:
                    nextFragment = new CategoriesListFragment();
                    title = "Категории";
                    break;
                // Calendar
                case 2:
                    nextFragment = new CalendarFragment();
                    title = "Календарь активностей";
                    break;
                // Statistic
                case 3:
                    nextFragment = new StatisticFragment();
                    title = "Статистика";
                    break;

                // Settings
                case 4:
                    nextFragment = new SettingsFragment();
                    title = "Настройки";
                    break;
                // Tasks list
                case 20:
                    nextFragment = new CardsListFragment();
                    break;
            }
        }
        else{
            //nextFragment = new CategoriesListFragment();
            nextFragment = new CalendarFragment();
            //nextFragment = new StatisticFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container_view, nextFragment)
                .commit();
        toolbar.setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment nextFragment = null;
        String title = "";

        switch (item.getItemId()){
            case R.id.categories_item:
                nextFragment = new CategoriesListFragment();
                title = "Категории";
                App.getSettings().setCurrentWindow(1);
                break;
            case R.id.calendar_item:
                nextFragment = new CalendarFragment();
                title = "Календарь";
                App.getSettings().setCurrentWindow(2);
                break;
            case R.id.statistic_item:
                nextFragment = new StatisticFragment();
                title = "Статистика";
                App.getSettings().setCurrentWindow(3);
                break;
            case R.id.settings_item:
                nextFragment = new SettingsFragment();
                title = "Настройки";
                App.getSettings().setCurrentWindow(4);
                break;
        }

        if(nextFragment != null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment_container_view, nextFragment)
                    .addToBackStack(null)
                    .commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        toolbar.setTitle(title);

        return false;
    }

    private void setupApplication(){

        if(App.getSettings().getSetupState() == false){

            // Themes installation

            List<Theme> defaultThemesList = new ArrayList<Theme>(20);

            defaultThemesList.add(new Theme(1, "Rajah",
                    "#FFB563", "#000000", 0));
            defaultThemesList.get(0).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(0).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(2, "Celadon",
                    "#BAF2BB", "#000000", 0));
            defaultThemesList.get(1).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(1).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(3, "Fiery Rose",
                    "#EF626C", "#FFFFFF", 0));
            defaultThemesList.get(2).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(2).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(4, "Orange Red Crayola",
                    "#FF5E5B", "#FFFFFF", 0));
            defaultThemesList.get(3).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(3).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(5, "Middle Green",
                    "#5B8C5A", "#FFFFFF", 0));
            defaultThemesList.get(4).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(4).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(6, "Sky Blue Crayola",
                    "#90E0F3", "#000000", 0));
            defaultThemesList.get(5).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(5).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(7, "Orange Yellow",
                    "#FABC2A", "#000000", 0));
            defaultThemesList.get(6).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(6).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(8, "Mauve Taupe",
                    "#925E78", "#FFFFFF", 0));
            defaultThemesList.get(7).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(7).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(9, "Cyan Process",
                    "#00A5E0", "#FFFFFF", 0));
            defaultThemesList.get(8).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(8).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(10, "Orchid Crayola",
                    "#EF9CDA", "#000000", 0));
            defaultThemesList.get(9).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(9).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(11, "Black Coffee",
                    "#32292F", "#FFFFFF", 0));
            defaultThemesList.get(10).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(10).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(12, "Umber",
                    "#705D56", "#FFFFFF", 0));
            defaultThemesList.get(11).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(11).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(13, "Ocean Green",
                    "#65B891", "#FFFFFF", 0));
            defaultThemesList.get(12).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(12).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(14, "Pastel Pink",
                    "#DAA89B", "#000000", 0));
            defaultThemesList.get(13).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(13).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(15, "Blue Bell",
                    "#998FC7", "#FFFFFF", 0));
            defaultThemesList.get(14).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(14).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(16, "Steel Teal",
                    "#6E8387", "#FFFFFF", 0));
            defaultThemesList.get(15).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(15).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(17, "Caput Mortuum",
                    "#522A27", "#FFFFFF", 0));
            defaultThemesList.get(16).setTextColor(getColor(R.color.text_main));
            defaultThemesList.get(16).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(18, "Satin Sheen Gold",
                    "#C59849", "#FFFFFF", 0));
            defaultThemesList.get(17).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(17).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(19, "United Nations Blue",
                    "#6290C3", "#FFFFFF", 0));
            defaultThemesList.get(18).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(18).setIconColor(getColor(R.color.text_main));

            defaultThemesList.add(new Theme(20, "Mandarin",
                    "#F37748", "#FFFFFF", 0));
            defaultThemesList.get(19).setTextColor(getColor(R.color.text_light));
            defaultThemesList.get(19).setIconColor(getColor(R.color.text_main));

            for (Theme theme: defaultThemesList) {
                mViewModel.addTheme(theme);
            }

            // Categories installation
            List<Category> defaultCategoriesList = new ArrayList<Category>(4);
            defaultCategoriesList.add(new Category("Дом",
                    "Домашние дела, покупки и т.д.", ""));
            defaultCategoriesList.get(0).setThemeID(13);

            defaultCategoriesList.add(new Category("Работа",
                    "Будильники, встречи и т.д.", ""));
            defaultCategoriesList.get(1).setThemeID(14);

            defaultCategoriesList.add(new Category("Прочее",
                    "Все остальное", ""));
            defaultCategoriesList.get(2).setThemeID(10);

            defaultCategoriesList.add(new Category("Спорт / Здоровье",
                    "", ""));
            defaultCategoriesList.get(3).setThemeID(12);

            for (Category category: defaultCategoriesList) {
                mViewModel.addCategory(category);
            }

            // Some projects installation
            List<Project> defaultProjectsList = new ArrayList<Project>(5);
            defaultProjectsList.add(new Project("Вылечить зуб", "", 1));
            defaultProjectsList.add(new Project("Сделать презентацию", "Способы оптимизации алгоритмов", 2));
            defaultProjectsList.get(1).setRange("24.01.2022", "29.01.2022");

            for (Project project: defaultProjectsList) {
                mViewModel.addProject(project);
            }

            // Some Tasks installation
            List<Task> defaultTasksList = new ArrayList<Task>(5);
            Calendar calendar = GregorianCalendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            try {
                calendar.setTime(sdf.parse("25.01.2022 00:00:00"));
            } catch (ParseException e){
                Log.e("INITIAL SETUP ERROR: ", e.getMessage());
            }

            defaultTasksList.add(new Task("Оплатить счета", "Оплатить счета за дом", 1 ));
            defaultTasksList.get(0).setAlarmTime("25.01.2022 11:20");
            defaultTasksList.get(0).setNotifyEnabled(true);
            defaultTasksList.get(0).setRepeatMode(4);
            defaultTasksList.get(0).setImportant(true);

            defaultTasksList.add(new Task("Полить цветы", "Полить все цветы кроме, замиокулькаса", 1 ));
            defaultTasksList.get(1).setAlarmTime("25.01.2022 17:40");
            defaultTasksList.get(1).setNotifyEnabled(true);
            defaultTasksList.get(1).setRepeatMode(3);
            defaultTasksList.get(1).setTuesday(true);
            defaultTasksList.get(1).setThemeID(1);

            defaultTasksList.add(new Task("Забрать посылку", "", 1 ));
            defaultTasksList.get(2).setAlarmTime("26.01.2022 16:00");
            defaultTasksList.get(2).setNotifyEnabled(true);
            defaultTasksList.get(2).setThemeID(2);
            defaultTasksList.get(2).setImportant(true);

            defaultTasksList.add(new Task("Утренняя разминка", "", 4 ));
            defaultTasksList.get(3).setAlarmTime("26.01.2022 10:00");
            defaultTasksList.get(3).setNotifyEnabled(true);
            defaultTasksList.get(3).setRepeatMode(3);
            defaultTasksList.get(3).setMonday(true);
            defaultTasksList.get(3).setWednesday(true);
            defaultTasksList.get(3).setFriday(true);
            defaultTasksList.get(3).setThemeID(3);

            defaultTasksList.add(new Task("Забрать ключи", "Ключи от офиса 303", 2 ));
            defaultTasksList.get(4).setAlarmTime("26.01.2022 15:00");
            defaultTasksList.get(4).setNotifyEnabled(true);
            defaultTasksList.get(4).setThemeID(1);

            defaultTasksList.add(new Task("Отправиться на прием к врачу", "Кабинет 6", 4 ));
            defaultTasksList.get(5).setAlarmTime("11.01.2022 7:40");
            defaultTasksList.get(5).setNotifyEnabled(true);
            defaultTasksList.get(5).setRepeatMode(1);
            defaultTasksList.get(5).setThemeID(2);

            // Project tasks
            defaultTasksList.add(new Task("Поискать номер регистратуры", "", 1 ));
            defaultTasksList.get(6).setParentID(defaultProjectsList.get(0).getProjectId());

            defaultTasksList.add(new Task("Позвонить по номеру", "", 1 ));
            defaultTasksList.get(7).setParentID(defaultProjectsList.get(0).getProjectId());

            defaultTasksList.add(new Task("Записать дату приема", "", 1 ));
            defaultTasksList.get(8).setParentID(defaultProjectsList.get(0).getProjectId());


            // Project tasks
            defaultTasksList.add(new Task("Подготовить литературу", "Поискать на programmer-lib", 2 ));
            defaultTasksList.get(9).setAlarmTime("24.01.2022 11:00");
            defaultTasksList.get(9).setNotifyEnabled(true);
            defaultTasksList.get(9).setParentID(defaultProjectsList.get(1).getProjectId());

            defaultTasksList.add(new Task("Определить структуру", "3 раздела, 12 слайдов", 2 ));
            defaultTasksList.get(10).setAlarmTime("25.01.2022 11:00");
            defaultTasksList.get(10).setNotifyEnabled(true);
            defaultTasksList.get(10).setParentID(defaultProjectsList.get(1).getProjectId());
            defaultTasksList.get(10).setImportant(true);

            defaultTasksList.add(new Task("Написать текст", "8 страниц, 14пт", 2 ));
            defaultTasksList.get(11).setAlarmTime("27.01.2022 17:00");
            defaultTasksList.get(11).setNotifyEnabled(true);
            defaultTasksList.get(11).setParentID(defaultProjectsList.get(1).getProjectId());

            defaultTasksList.add(new Task("Проверить новую версию Room", "Последняя 2.4.1", 2 ));
            defaultTasksList.get(12).setAlarmTime("20.02.2022 17:00");
            defaultTasksList.get(12).setNotifyEnabled(true);
            defaultTasksList.get(12).setParentID(defaultProjectsList.get(1).getProjectId());

            for (Task task: defaultTasksList) {
                mViewModel.addTask(task);
            }

            //installRandomStats(1000);

            Log.d("MAIN_ACT: ", "INITIAL SETUP COMPLETED");
            App.getSettings().setSetupState(true);
        }
        else{
            return;
        }
    }
    private void installRandomStats(int count){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        long epochIntervalStartSeconds = LocalDateTime.of(2021, 5, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long epochIntervalEndSeconds = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC);
        List<UserCaseStatistic> ucsList = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            UserCaseStatistic ucs = new UserCaseStatistic("TEST" + String.valueOf(i), false, false);
            ucs.setSecondsRecordDateTime(ThreadLocalRandom
                    .current()
                    .nextLong(epochIntervalStartSeconds, epochIntervalEndSeconds));
            int completedOrExpired = ThreadLocalRandom
                    .current()
                    .nextInt(0, 10);
            if(completedOrExpired > 4){
                ucs.setStateCompleted(true);
            }
            else{
                ucs.setStateExpired(true);
            }
            ucsList.add(ucs);
            Log.d("UCS: ", LocalDateTime.ofEpochSecond((long) ucs.getMillisRecordDateTime()/1000, 0, ZoneOffset.UTC).format(dtf));
            mViewModel.addUserCaseStatisticSilently(ucs);
        }
        Log.d("MAIN_ACT: ", "USER STATS TABLE FILLED");
    }
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
        mViewModel.clean();
    }
}