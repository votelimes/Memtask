package com.example.clock.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.databinding.ActivityMainBinding;
import com.example.clock.fragments.AboutFragment;
import com.example.clock.fragments.CalendarFragment;
import com.example.clock.fragments.CardsListFragment;
import com.example.clock.fragments.CategoriesListFragment;
import com.example.clock.fragments.SettingsFragment;
import com.example.clock.fragments.StatisticFragment;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.TaskNotificationManager;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
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
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //ViewModel and binding
    MainViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityMainBinding mActivityBinding;

    //Menu
    MaterialToolbar toolbar;
    public DrawerLayout drawerLayout;
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
        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getColor(R.color.backgroundSecondary));
        toolbar.setTitleTextColor(getColor(R.color.toolbarTitle));
        toolbar.setSubtitleTextColor(getColor(R.color.toolbarIcons));

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

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.categories_item);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getColor(R.color.textPrimary)), 0, s.length(), 0);
        s.setSpan(new AbsoluteSizeSpan(14, true), 0, s.length(), 0);
        menuItem.setTitle(s);

        menuItem = navigationView.getMenu().findItem(R.id.calendar_item);
        s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getColor(R.color.textPrimary)), 0, s.length(), 0);
        s.setSpan(new AbsoluteSizeSpan(14, true), 0, s.length(), 0);
        menuItem.setTitle(s);

        menuItem = navigationView.getMenu().findItem(R.id.statistic_item);
        s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getColor(R.color.textPrimary)), 0, s.length(), 0);
        s.setSpan(new AbsoluteSizeSpan(14, true), 0, s.length(), 0);
        menuItem.setTitle(s);

        menuItem = navigationView.getMenu().findItem(R.id.settings_item);
        s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getColor(R.color.textPrimary)), 0, s.length(), 0);
        s.setSpan(new AbsoluteSizeSpan(14, true), 0, s.length(), 0);
        menuItem.setTitle(s);

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
                    navigationView.setCheckedItem(R.id.categories_item);
                    break;
                // Calendar
                case 2:
                    nextFragment = new CalendarFragment();
                    title = "Календарь активностей";
                    navigationView.setCheckedItem(R.id.calendar_item);
                    break;
                // Statistic
                case 3:
                    nextFragment = new StatisticFragment();
                    title = "Статистика";
                    navigationView.setCheckedItem(R.id.statistic_item);
                    break;

                // Settings
                case 4:
                    nextFragment = new SettingsFragment();
                    title = "Настройки";
                    navigationView.setCheckedItem(R.id.settings_item);
                    break;
                // Tasks list
                case 20:
                    nextFragment = new CardsListFragment();
                    navigationView.setCheckedItem(R.id.categories_item);
                    break;
            }
        }
        else{
            nextFragment = new CategoriesListFragment();
            //nextFragment = new CalendarFragment();
            //nextFragment = new StatisticFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_exit, R.anim.fragment_enter)
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container_view, nextFragment)
                .commit();
        toolbar.setTitle(title);

        ConstraintLayout mh = (ConstraintLayout) navigationView.getHeaderView(0);
        mh.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String title = "";

        navigationView.getMenu().findItem(R.id.categories_item).setChecked(false);
        navigationView.getMenu().findItem(R.id.calendar_item).setChecked(false);
        navigationView.getMenu().findItem(R.id.statistic_item).setChecked(false);
        navigationView.getMenu().findItem(R.id.settings_item).setChecked(false);

        item.setChecked(true);

        FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()){
            case R.id.categories_item:
                //nextFragment = new CategoriesListFragment();
                ftr = ftr.replace(R.id.main_fragment_container_view, CategoriesListFragment.class, null);
                title = "Категории";
                App.getSettings().setCurrentWindow(1);
                break;
            case R.id.calendar_item:
                //nextFragment = new CalendarFragment();
                ftr = ftr.replace(R.id.main_fragment_container_view, CalendarFragment.class, null);
                title = "Календарь";
                App.getSettings().setCurrentWindow(2);
                break;
            case R.id.statistic_item:
                //nextFragment = new StatisticFragment();
                ftr = ftr.replace(R.id.main_fragment_container_view, StatisticFragment.class, null);
                title = "Статистика";
                App.getSettings().setCurrentWindow(3);
                break;
            case R.id.settings_item:
                //nextFragment = new SettingsFragment();
                ftr = ftr.replace(R.id.main_fragment_container_view, SettingsFragment.class, null);
                title = "Настройки";
                App.getSettings().setCurrentWindow(4);
                break;
        }
        ftr
        /*.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit,
                R.anim.fragment_pop_enter, R.anim.fragment_pop_exit)*/
        .setReorderingAllowed(false)
        //.replace(R.id.main_fragment_container_view, nextFragment)
        .addToBackStack(null)
        .commit();

        drawerLayout.closeDrawer(GravityCompat.START);

        toolbar.setTitle(title);

        return false;
    }

    public void setupNav(){
        Drawable navIcon = getDrawable(R.drawable.ic_round_menu_24);
        navIcon.setTint(getColor(R.color.primary));

        toolbar.setNavigationIcon(navIcon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });
    }

    private void setupApplication(){

        if(App.getSettings().getSetupState() == false){
            // NotificationChannels installation
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            NotificationChannel alarmChannel = new NotificationChannel(TaskNotificationManager.NOTIFICATION_ALARM_CHANNEL_ID, TaskNotificationManager.NOTIFICATION_ALARM_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            alarmChannel.setDescription(TaskNotificationManager.NOTIFICATION_ALARM_CHANNEL_DESCRIPTION);
            alarmChannel.enableLights(true);
            alarmChannel.enableVibration(true);
            notificationManager.createNotificationChannel(alarmChannel);

            NotificationChannel generalChannel = new NotificationChannel(TaskNotificationManager.NOTIFICATION_GENERAL_CHANNEL_ID, TaskNotificationManager.NOTIFICATION_GENERAL_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            generalChannel.setDescription(TaskNotificationManager.NOTIFICATION_GENERAL_CHANNEL_DESCRIPTION);
            generalChannel.enableLights(false);
            generalChannel.enableVibration(false);
            notificationManager.createNotificationChannel(generalChannel);

            // Themes installation

            List<Theme> defaultThemesList = new ArrayList<Theme>(20);

            defaultThemesList.add(new Theme("Rajah",
                    "#FFB563", "#68B0AB", 0));
            defaultThemesList.get(0).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(0).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("Celadon",
                    "#BAF2BB", "#F47B93", 0));
            defaultThemesList.get(1).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(1).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("Fiery Rose",
                    "#EF626C", "#05A8AA", 0));
            defaultThemesList.get(2).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(2).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(2).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Orange Red Crayola",
                    "#FF5E5B", "#50B2C0", 0));
            defaultThemesList.get(3).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(3).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(3).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Middle Green",
                    "#5B8C5A", "#311E10", 0));
            defaultThemesList.get(4).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(4).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(4).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Sky Blue Crayola",
                    "#90E0F3", "#311E10", 0));
            defaultThemesList.get(5).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(5).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("Orange Yellow",
                    "#FABC2A", "#662C91", 0));
            defaultThemesList.get(6).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(6).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("Mauve Taupe",
                    "#925E78", "#F15152", 0));
            defaultThemesList.get(7).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(4).setAdditionalTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(7).setIconColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Cyan Process",
                    "#00A5E0", "#464D77", 0));
            defaultThemesList.get(8).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(8).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(8).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Orchid Crayola",
                    "#EF9CDA", "#568259", 0));
            defaultThemesList.get(9).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(9).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("Black Coffee",
                    "#32292F", "#FF715B", 0));
            defaultThemesList.get(10).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(10).setIconColor(getColor(R.color.act_text_light));
            defaultThemesList.get(10).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Umber",
                    "#705D56", "#58BC82", 0));
            defaultThemesList.get(11).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(11).setIconColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Ocean Green",
                    "#65B891", "#FF674D", 0));
            defaultThemesList.get(12).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(12).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(12).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Pastel Pink",
                    "#DAA89B", "#F15152", 0));
            defaultThemesList.get(13).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(13).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("Blue Bell",
                    "#998FC7", "#F15152", 0));
            defaultThemesList.get(14).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(14).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(14).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Steel Teal",
                    "#6E8387", "#F15152", 0));
            defaultThemesList.get(15).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(15).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(15).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Caput Mortuum",
                    "#522A27", "#B96D40", 0));
            defaultThemesList.get(16).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(16).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(16).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Satin Sheen Gold",
                    "#C59849", "#549F93", 0));
            defaultThemesList.get(17).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(17).setIconColor(getColor(R.color.act_text_grey));
            defaultThemesList.get(17).setAdditionalTextColor(getColor(R.color.act_text_light));


            defaultThemesList.add(new Theme("United Nations Blue",
                    "#6290C3", "#549F93", 0));
            defaultThemesList.get(18).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(18).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(18).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("Mandarin",
                    "#F37748", "#549F93", 0));
            defaultThemesList.get(19).setMainTextColor(getColor(R.color.act_text_light));
            defaultThemesList.get(19).setIconColor(getColor(R.color.act_text_main));
            defaultThemesList.get(19).setAdditionalTextColor(getColor(R.color.act_text_light));

            defaultThemesList.add(new Theme("MainTaskTheme",
                    "#F7EDE2", "#F15152", 0));
            defaultThemesList.get(20).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(20).setIconColor(getColor(R.color.act_text_main));

            defaultThemesList.add(new Theme("MainProjectTheme",
                    "#F7EDE2", "#F15152", 0));
            defaultThemesList.get(21).setMainTextColor(getColor(R.color.act_text_main));
            defaultThemesList.get(21).setIconColor(getColor(R.color.act_text_main));

            for (Theme theme: defaultThemesList) {
                if(theme.getAdditionalTextColor() == -1){
                    theme.setAdditionalTextColor(getColor(R.color.act_text_grey));
                }

                theme.setBaseTheme(true);
                mViewModel.addTheme(theme);
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
                mViewModel.addCategory(category);
            }

            // Some projects installation
            List<Project> defaultProjectsList = new ArrayList<Project>(5);
            defaultProjectsList.add(new Project("Вылечить зуб", "", 4));
            defaultProjectsList.get(0).setRange("24.02.2022", "28.02.2022");
            defaultProjectsList.get(0).setThemeID(defaultThemesList.get(21).getID());
            defaultProjectsList.add(new Project("Сделать презентацию", "Способы оптимизации алгоритмов", 2));
            defaultProjectsList.get(1).setRange("01.02.2022", "15.02.2022");
            defaultProjectsList.get(1).setThemeID(defaultThemesList.get(21).getID());

            for (Project project: defaultProjectsList) {
                mViewModel.addProject(project);
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
            defaultTasksList.get(0).setImportance(3);
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
            defaultTasksList.get(2).setImportance(0);

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
            defaultTasksList.get(5).setAlarmTime("11.02.2022 07:40");
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
            defaultTasksList.get(10).setImportance(3);
            defaultTasksList.get(10).setThemeID(defaultThemesList.get(12).getID());

            defaultTasksList.add(new Task("Написать текст", "8 страниц, 14пт", 2 ));
            defaultTasksList.get(11).setAlarmTime("27.02.2022 17:00");
            defaultTasksList.get(11).setNotificationEnabled(true);
            defaultTasksList.get(11).setParentID(defaultProjectsList.get(1).getProjectId());
            defaultTasksList.get(11).setThemeID(defaultThemesList.get(11).getID());

            defaultTasksList.add(new Task("Проверить новую версию Room", "Последняя 2.4.1", 2 ));
            defaultTasksList.get(12).setAlarmTime("20.02.2022 17:00");
            defaultTasksList.get(12).setNotificationEnabled(false);
            defaultTasksList.get(12).setExpired(true);
            defaultTasksList.get(12).setThemeID(defaultThemesList.get(10).getID());

            defaultTasksList.add(new Task("Посмотреть видео про коптеры", "Любое", 3 ));
            defaultTasksList.get(13).setThemeID(defaultThemesList.get(6).getID());

            defaultTasksList.add(new Task("Найти книгу с голубым переплетом", "Смотрел в гостинной, в третьем шкафу. Посмотреть в кладовке.", 3 ));
            defaultTasksList.get(14).setThemeID(defaultThemesList.get(7).getID());

            defaultTasksList.add(new Task("Потратить баллы юмани", "Кол-во баллов на февраль 540", 3 ));
            defaultTasksList.get(15).setThemeID(defaultThemesList.get(8).getID());

            defaultTasksList.add(new Task("Определить планы на лето", "1. Похудеть." +
                    "2. ...", 3 ));
            defaultTasksList.get(16).setThemeID(defaultThemesList.get(14).getID());

            defaultTasksList.add(new Task("Тексты", "Сделать пакет текстов Party-box", 3 ));
            defaultTasksList.get(17).setThemeID(defaultThemesList.get(12).getID());

            defaultTasksList.add(new Task("Обложки", "Придумать новые обложки для дисков", 3 ));
            defaultTasksList.get(18).setThemeID(defaultThemesList.get(13).getID());

            defaultTasksList.add(new Task("Тренинг", "Найти тренинг по аналитике", 3 ));
            defaultTasksList.get(19).setThemeID(defaultThemesList.get(11).getID());

            for (Task task: defaultTasksList) {
                mViewModel.addTask(task);
            }

            installRandomStats(1000);

            Log.d("MAIN_ACT: ", "INITIAL SETUP COMPLETED");
            App.getSettings().setSetupState(true);

            if(App.isTesting()){

                List<Project> testProjList = new ArrayList<>(10);
                for(int i = 0; i < 10; i++){
                    Project proj = new Project("Project #" + String.valueOf(i+1), "", 1);
                    proj.setThemeID(defaultThemesList.get(20).getID());

                    Task task1 = new Task("Task #" + String.valueOf(i+1) + "*1", "test", 1);
                    task1.setThemeID(defaultThemesList.get(20).getID());
                    task1.setParentID(proj.getProjectId());

                    Task task2 = new Task("Task #" + String.valueOf(i+1) + "*2", "test", 1);
                    task2.setThemeID(defaultThemesList.get(20).getID());
                    task2.setParentID(proj.getProjectId());

                    testProjList.add(proj);
                    mViewModel.addProject(proj);
                    mViewModel.addTask(task1);
                    mViewModel.addTask(task2);
                }

                for(int i = 0; i < 10000; i++){
                    Task task = new Task("Task #" + String.valueOf(i+1), "test", 1);
                    task.setThemeID(defaultThemesList.get(20).getID());
                    if((i+1)%9 == 0){
                        task.setParentID(testProjList.get(9).getProjectId());
                    }
                    else if((i+1)%8 == 0){
                        task.setParentID(testProjList.get(8).getProjectId());
                    }
                    else if((i+1)%7 == 0){
                        task.setParentID(testProjList.get(7).getProjectId());
                    }
                    else if((i+1)%6 == 0){
                        task.setParentID(testProjList.get(6).getProjectId());
                    }
                    else if((i+1)%5 == 0){
                        task.setParentID(testProjList.get(5).getProjectId());
                    }
                    else if((i+1)%4 == 0){
                        task.setParentID(testProjList.get(4).getProjectId());
                    }
                    else if((i+1)%3 == 0){
                        task.setParentID(testProjList.get(3).getProjectId());
                    }
                    mViewModel.addTask(task);
                    SystemClock.sleep(10);
                }
            }
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
            //Log.d("UCS: ", LocalDateTime.ofEpochSecond((long) ucs.getMillisRecordDateTime()/1000, 0, ZoneOffset.UTC).format(dtf));
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

    @Override
    public void onClick(View view) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit,
                        R.anim.fragment_pop_enter, R.anim.fragment_pop_exit)
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container_view, new AboutFragment())
                .addToBackStack(null)
                .commit();
    }

}