package com.votelimes.memtask.services;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskNotificationManager;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;

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