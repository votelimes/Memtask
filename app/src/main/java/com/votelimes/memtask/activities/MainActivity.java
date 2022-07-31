package com.votelimes.memtask.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.databinding.ActivityMainBinding;
import com.votelimes.memtask.fragments.AboutFragment;
import com.votelimes.memtask.fragments.CalendarFragment;
import com.votelimes.memtask.fragments.CardsListFragment;
import com.votelimes.memtask.fragments.CategoriesListFragment;
import com.votelimes.memtask.fragments.SettingsFragment;
import com.votelimes.memtask.fragments.StatisticFragment;
import com.votelimes.memtask.model.InitialSetup;
import com.votelimes.memtask.viewmodels.MainViewModel;
import com.votelimes.memtask.viewmodels.ViewModelFactoryBase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

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

        InitialSetup.setup(this, mViewModel);

        Fragment nextFragment = null;
        String title = "";
        if(!App.getSettings().getLastCategory().first.equals("")) {
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
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_exit, R.anim.fragment_enter)
                .setReorderingAllowed(true)
                .replace(R.id.main_fragment_container_view, nextFragment)
                .commit();
        toolbar.setTitle(title);

        ConstraintLayout mh = (ConstraintLayout) navigationView.getHeaderView(0);
        mh.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, 100);
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                drawerLayout.requestFocus();
                drawerLayout.open();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.syncGCREAD(getBaseContext(), this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getAttributes().token, 0);
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

        .setReorderingAllowed(false)
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

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    ActivityResultLauncher<String[]> contactsPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean readContacts = result.getOrDefault(
                                Manifest.permission.READ_CONTACTS, false);
                    }
            );
}