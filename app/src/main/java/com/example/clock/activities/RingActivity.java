package com.example.clock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.R;
import com.example.clock.adapters.CardsListFragmentAdapter;
import com.example.clock.app.App;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.services.AlarmService;
import com.example.clock.storageutils.Tuple3;
import com.example.clock.viewmodels.ManageTaskViewModel;
import com.example.clock.viewmodels.RingViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;

import java.util.Calendar;
import java.util.List;

public class RingActivity extends AppCompatActivity {

    ViewModelFactoryBase mFactory;
    RingViewModel mViewModel;
    String taskID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        taskID = getIntent().getStringExtra("taskID");
        mFactory = new ViewModelFactoryBase(
                getApplication(),
                App.getDatabase(),
                App.getSilentDatabase(),
                taskID
        );
        mViewModel = new ViewModelProvider(this, mFactory).get(RingViewModel.class);
        mViewModel.setTaskID(taskID);
    }

    public void onStop(View view) {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);

        finish();
    }

    public void onSnooze10(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);

        mViewModel.getCurrentTask().getValue().schedule(getApplicationContext());

        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }
    final Observer<Task> hoardObserver = new Observer<Task>() {
        @Override
        public void onChanged(@Nullable final Task currentTask) {
            TextView name = (TextView) findViewById(R.id.ring_name_text);
            name.setText(currentTask.getName());
        }
    };
}