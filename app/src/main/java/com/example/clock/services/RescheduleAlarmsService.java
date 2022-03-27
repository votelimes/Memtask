package com.example.clock.services;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

public class RescheduleAlarmsService extends LifecycleService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        /*!!! CHECK !!!*/
        /*App.getAlarmsLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                for (Task a : tasks) {
                    if (a.isStarted()) {
                        a.schedule(getApplicationContext());
                    }
                }
            }
        });*/

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}