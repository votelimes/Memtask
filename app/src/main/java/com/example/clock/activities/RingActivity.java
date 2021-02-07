package com.example.clock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clock.R;
import com.example.clock.data.Alarm;
import com.example.clock.services.AlarmService;

import java.util.Calendar;

public class RingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
    }

    public void onStop(View view) {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    public void onSnooze10(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);

        Alarm alarm = new Alarm(calendar, 0);
        alarm.setNote("Snooze");
        alarm.schedule(getApplicationContext());

        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }
}