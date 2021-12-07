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
import com.example.clock.fragments.CardsListFragment;
import com.example.clock.fragments.CategoriesListFragment;
import com.example.clock.fragments.SettingsFragment;
import com.example.clock.model.Category;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.example.clock.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
        mFactory = new ViewModelFactoryBase(getApplication(), App.getDatabase());
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
        if(App.getSettings().getLastCategoryID() != -1) {
            switch ((int) App.getSettings().getCurrentWindow()) {
                // Calendar
                case 0:

                    break;

                // Categories list
                case 1:
                    nextFragment = new CategoriesListFragment();
                    break;

                // Tasks list
                case 2:
                    nextFragment = new CardsListFragment();
                    break;

                // Statistic
                case 3:

                    break;

                // Settings
                case 4:
                    nextFragment = new SettingsFragment();
                    break;

            }
        }
        else{
            nextFragment = new CategoriesListFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container_view, nextFragment)
                .addToBackStack(null)
                .commit();
    }

    /*private void updateMainClock(){
        TextView timeView = (TextView) findViewById(R.id.mainClockTextView);
        TextView timeViewIndex = (TextView) findViewById(R.id.mainClockTimeIndex);
        if(App.Settings.is24HTimeUses) {
            SimpleDateFormat hoursFormatter = new SimpleDateFormat("HH:mm");
            timeView.setText(hoursFormatter.format(new Date()));
            timeViewIndex.setText("");
        }
        else{
            SimpleDateFormat hoursFormatter = new SimpleDateFormat("hh:mm aa");
            String timeString = hoursFormatter.format(new Date());
            String[] time = timeString.split(" ");

            timeView.setText(time[0]);
            timeViewIndex.setText(time[1]);
        }
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment nextFragment = null;
        String title = "testActionBar";

        switch (item.getItemId()){
            case R.id.categories_item:
                nextFragment = new CategoriesListFragment();
                title = "Категории";
                App.getSettings().setCurrentWindow(1);
                break;
            case R.id.test_list:
                nextFragment = new CardsListFragment();
                title = "ТЕСТ";
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

            defaultThemesList.add(new Theme(1, "Celadon",
                    "#BAF2BB", "#000000", 0));

            defaultThemesList.add(new Theme(2, "Celadon",
                    "#BAF2BB", "#000000", 0));

            defaultThemesList.add(new Theme(3, "Fiery Rose",
                    "#EF626C", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(4, "Orange Red Crayola",
                    "#FF5E5B", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(5, "Middle Green",
                    "#5B8C5A", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(6, "Sky Blue Crayola",
                    "#90E0F3", "#000000", 0));

            defaultThemesList.add(new Theme(7, "Orange Yellow",
                    "#FABC2A", "#000000", 0));

            defaultThemesList.add(new Theme(8, "Mauve Taupe",
                    "#925E78", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(9, "Cyan Process",
                    "#00A5E0", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(10, "Orchid Crayola",
                    "#EF9CDA", "#000000", 0));

            defaultThemesList.add(new Theme(11, "Black Coffee",
                    "#32292F", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(12, "Umber",
                    "#705D56", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(13, "Ocean Green",
                    "#65B891", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(14, "Pastel Pink",
                    "#DAA89B", "#000000", 0));

            defaultThemesList.add(new Theme(15, "Blue Bell",
                    "#998FC7", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(16, "Steel Teal",
                    "#6E8387", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(17, "Caput Mortuum",
                    "#522A27", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(18, "Satin Sheen Gold",
                    "#C59849", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(19, "United Nations Blue",
                    "#6290C3", "#FFFFFF", 0));

            defaultThemesList.add(new Theme(20, "Mandarin",
                    "#F37748", "#FFFFFF", 0));

            for (Theme theme: defaultThemesList) {
                mViewModel.addTheme(theme);
            }

            // Categories installation
            List<Category> defaultCategoriesList = new ArrayList<Category>(3);
            defaultCategoriesList.add(new Category("Дом",
                    "Домашние дела, покупки и т.д.", ""));
            defaultCategoriesList.get(0).setThemeID(13);
            defaultCategoriesList.add(new Category("Работа",
                    "Будильники, встречи и т.д.", ""));
            defaultCategoriesList.get(1).setThemeID(14);

            for (Category category: defaultCategoriesList) {
                mViewModel.addCategory(category);
            }
            // Some Tasks installation
            List<Task> defaultTasksList = new ArrayList<Task>(5);
            Calendar calendar = GregorianCalendar.getInstance();

            defaultTasksList.add(new Task(calendar, 0, 1 ));
            defaultTasksList.get(0).setName("Оплатить счета");
            defaultTasksList.get(0).setDescription("Оплатить счета за дом");

            defaultTasksList.add(new Task(calendar, 0, 1 ));
            defaultTasksList.get(1).setName("Полить цветы");
            defaultTasksList.get(1).setDescription("Полить все цветы кроме Замиокулькаса");

            defaultTasksList.add(new Task(calendar, 0, 2 ));
            defaultTasksList.get(2).setName("Утренний будильник");
            defaultTasksList.get(2).setDescription("");

            defaultTasksList.add(new Task(calendar, 0, 2 ));
            defaultTasksList.get(3).setName("Планерка");
            defaultTasksList.get(3).setDescription("Пусто");

            for (Task task: defaultTasksList) {
                mViewModel.addTask(task);
            }

            Log.d("MAIN_ACT: ", "INITIAL SETUP COMPLETED");
            App.getSettings().setSetupState(true);
        }
        else{
            return;
        }
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
    }
}