package com.votelimes.memtask.broadcastreceiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.TaskNotificationManager;
import com.votelimes.memtask.model.UserCaseStatistic;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.services.AlarmService;
import com.votelimes.memtask.services.RescheduleAlarmsService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    MemtaskRepositoryBase mRepository;
    Task task;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startRescheduleAlarmsService(context);
        }
        if(Intent.ACTION_TIME_TICK.equals(intent.getAction())
                || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())
        || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())){
            startRescheduleAlarmsService(context);
        }
        if(false && intent.getIntExtra(TaskNotificationManager.MODE_KEY, -1) == TaskNotificationManager.MODE_INLINE){
            mRepository = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());
            String taskID = intent.getStringExtra(TaskNotificationManager.ID_KEY);
            task = mRepository.getTaskSilently(taskID);
            if(task == null){
                task = mRepository.getTaskSilently(taskID);
                if(task == null){
                    Log.d("WARNING: ", "BroadCastReceiver TASK database request are empty");
                    cancelAlarm(context, intent);
                    return;
                }
            }
            Intent alarmService = new Intent(App.getInstance(), AlarmService.class);
            if(intent.getIntExtra(TaskNotificationManager.NOTIFICATION_ALARM_REQUEST_CODE, -1) == TaskNotificationManager.NOTIFICATION_ALARM_COMPLETE){
                task.setCompleted(true);
                mRepository.addTask(task);
                mRepository.addUserCaseStatisticSilently(new UserCaseStatistic(task.getTaskId(), true, false));
                alarmService.putExtra(AlarmService.STOP_KEY, AlarmService.STOP_BY_USER);
                context.startForegroundService(alarmService);

            }
            else if(intent.getIntExtra(TaskNotificationManager.NOTIFICATION_ALARM_REQUEST_CODE, -1) == TaskNotificationManager.NOTIFICATION_ALARM_POSTPONE){

            }
            else if(intent.getIntExtra(TaskNotificationManager.NOTIFICATION_ALARM_REQUEST_CODE, -1) == TaskNotificationManager.NOTIFICATION_ALARM_SKIP){
                task.setCompleted(false);
                task.setExpired(true);
                mRepository.addTask(task);
                mRepository.addUserCaseStatisticSilently(new UserCaseStatistic(task.getTaskId(), false, true));
                alarmService.putExtra(AlarmService.STOP_KEY, AlarmService.STOP_BY_USER);
                context.startForegroundService(alarmService);
            }
            else if (alarmIsToday(task)) {
                startAlarmService(context, task.getTaskId());
            }
        }
        if(true || intent.getIntExtra(TaskNotificationManager.MODE_KEY, -1) == TaskNotificationManager.MODE_GENERAL){
            String taskID = intent.getStringExtra(TaskNotificationManager.ID_KEY);
            mRepository = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());
            task = mRepository.getTaskSilently(taskID);
            NotificationManager manager = (NotificationManager) App.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(App.getInstance(), TaskNotificationManager.NOTIFICATION_ALARM_CHANNEL_ID);

            Notification notification;
            notificationBuilder = notificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(task.getName())
                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setColor(App.getInstance().getColor(R.color.main_8));
            manager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

        }
    }

    private boolean alarmIsToday(Task task) {
        LocalDateTime time = LocalDateTime
                .ofEpochSecond((long) task.getNotificationStartMillis() / 1000,
                0, ZoneOffset.UTC);
        LocalDateTime today = LocalDateTime.now();
        if(task.getRepeatMode() == 0 || task.getRepeatMode() == 1){
            if(time.toLocalDate().isEqual(today.toLocalDate())){
                return true;
            }
        }
        else if(task.getRepeatMode() == 2 || task.getRepeatMode() == 3){
            if(task.isDayOfWeekActive(today.getDayOfWeek())) {
                return true;
            }
        }
        return false;
    }

    private void startAlarmService(Context context, String taskID) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.setAction(taskID);
        intentService.putExtra(TaskNotificationManager.ID_KEY, taskID);
        context.startForegroundService(intentService);
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        context.startForegroundService(intentService);
    }

    private void cancelAlarm(Context context, Intent intent) {
        PendingIntent alarmPendingIntent = PendingIntent
                .getBroadcast(context, TaskNotificationManager.REQUEST_CODE_BASE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmPendingIntent);
    }
}
