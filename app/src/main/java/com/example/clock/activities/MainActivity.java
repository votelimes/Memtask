package com.example.clock.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.R;
import com.example.clock.fragments.CardsListFragment;
import com.example.clock.fragments.CategoriesListFragment;
import com.example.clock.fragments.DefaultListFragment;
import com.example.clock.model.Category;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.example.clock.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    //Unpack result
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        mFactory = new ViewModelFactoryBase(getApplication());
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



        /*final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                populateDBTasks(2, mViewModel);
            }
        }, 5000);*/

        /*final Handler handler2 = new Handler(Looper.getMainLooper());
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                printDBTasks(mViewModel);
            }
        }, 10000);*/

        LiveData<List<Task>> testLD = mViewModel.requestTasksData();
        mViewModel.requestTasksData().observe(this, new Observer<List<Task>>() {
            // СДЕЛАЙ ВСЕ ЗАМЕНЫ ОБЪЕКТОВ В БД ТОЛЬКО ПО ID ИНАЧЕ НЕ РАБОТАЕТ И ЭТО ПРАВИЛЬНО
            @Override
            public void onChanged(List<Task> tasks) {
                LiveData<List<Task>> taskLD = mViewModel.requestTasksData();
                List<Task> data =  mViewModel.requestTasksData().getValue();

                int i = 0;
                i++;
            }
        });


    }

    private void printDBTasks(MainViewModel viewModel){
        LiveData<List<Task>> taskLD = mViewModel.requestTasksData();
        List<Task> data =  mViewModel.requestTasksData().getValue();



        Task task1 = mViewModel.getTask(1);
        Task task2 = mViewModel.getTask(2);
        Task task3 = mViewModel.getTask(3);
        Task task4 = mViewModel.getTask(4);

        for(Task value : data){
            Log.d("ID: ", String.valueOf(value.getTaskId()));
            Log.d("DESC: ", value.getDescription());
        }
    }

    private void populateDBTasks(int objectsCount, MainViewModel viewModel){

        Calendar calendar = GregorianCalendar.getInstance();
        for(int i = 0; i < objectsCount; i++){
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            Task task = new Task(calendar, 0, "Iteration #" + String.valueOf(i + 1), 0);
            viewModel.addTask(task);

            Log.d("ADD: ", "Task #" + String.valueOf(i + 1) + " ADDED");
        }
    }

    private void populateDBCategories(int objectsCount, MainViewModel viewModel){

        Calendar calendar = GregorianCalendar.getInstance();
        for(int i = 0; i < objectsCount; i++){
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            Category category = new Category("Category #" + String.valueOf(i + 1), "TEST");
            viewModel.addCategory(category);
        }
    }


    private void changeStrokeColor(View v, int color){
        Drawable background = (Drawable) v.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.mutate();
        gradientDrawable.setStroke(3, color);
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

    // Utility methods
    public int pxToDp(Context context, int px) {
        return  ((int) (px / context.getResources().getDisplayMetrics().density));
    }
    public String timeInMillisToTime(long timeInMillis){
        Date time = new Date(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(time);
    }

    protected void displayFragment(int viewId){

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment nextFragment = null;
        String title = "testActionBar";

        switch (item.getItemId()){
            case R.id.categories_item:
                nextFragment = new CategoriesListFragment();
                break;
            case R.id.test_list:
                nextFragment = new CardsListFragment();
                break;
        }

        if(nextFragment != null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment_container_view, nextFragment)
                    .commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        toolbar.setTitle(title);

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}