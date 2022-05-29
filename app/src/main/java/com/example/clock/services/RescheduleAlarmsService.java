package com.example.clock.services;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.example.clock.app.App;
import com.example.clock.model.Task;
import com.example.clock.model.TaskNotificationManager;
import com.example.clock.repositories.MemtaskRepositoryBase;

import java.util.List;

public class RescheduleAlarmsService extends LifecycleService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        MemtaskRepositoryBase repositoryBase = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());
        List<Task> taskList = repositoryBase.getAllTasks();
        taskList.forEach(task -> {
            if(task.isNotificationEnabled()) {
                task.schedule(App.getInstance().getApplicationContext());
            }
        });
        TaskNotificationManager.scheduleGeneralNotifications(App.getInstance().getApplicationContext());
        this.stopSelf();
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