package com.example.clock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.model.Task;
import com.example.clock.model.TaskNotificationManager;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.services.AlarmService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RingActivity extends AppCompatActivity {
    MemtaskRepositoryBase mRepository;
    Task task;
    String taskID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskID = getIntent().getStringExtra(TaskNotificationManager.ID_KEY);
        mRepository = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());
        task = mRepository.getTaskSilently(taskID);

        if (task == null) {
            Log.d("ERROR: ", "Unable to get task in RING ACTIVITY");
            finish();
            return;
        }
        setContentView(R.layout.activity_ring);

        task.setNotificationInProgress(true);

        ((TextView) findViewById(R.id.ring_name_text)).setText(task.getName());
        AppCompatButton snoozeButton = (AppCompatButton) findViewById(R.id.snooze10_button_ring);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSnooze10(view);
            }
        });
    }

    public void onStop(View view) {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        if(task.markIfExpired()){
            mRepository.addUserCaseStatisticSilently(
                    new UserCaseStatistic(task.getTaskId(), task.isCompleted(), task.isExpired()));
            mRepository.addTask(task);
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(task.markIfExpired()){
            mRepository.addUserCaseStatisticSilently(
                    new UserCaseStatistic(task.getTaskId(), task.isCompleted(), task.isExpired()));
            mRepository.addTask(task);
        }
    }

    public void onSnooze10(View view) {
        LocalDateTime now = LocalDateTime.now();
        long millisBackUp = task.getNotificationStartMillis();
        task.setNotificationStartMillis((now.toEpochSecond(ZoneOffset.UTC)) * 1000 + TaskNotificationManager.SNOOZE_TIME);
        task.schedule(view.getContext());
        task.setNotificationStartMillis(millisBackUp);
        mRepository.addTask(task);

        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }
}