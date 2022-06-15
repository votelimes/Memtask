package com.example.clock.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.clock.R;
import com.example.clock.activities.RingActivity;
import com.example.clock.app.App;
import com.example.clock.broadcastreceiver.AlarmBroadcastReceiver;
import com.example.clock.model.Task;
import com.example.clock.model.TaskNotificationManager;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.repositories.MemtaskRepositoryBase;

import java.io.IOException;

public class AlarmService extends Service {
    public static final String STOP_KEY = "Alarm_Service_stop_key";
    public static final int STOP_BY_USER = 401;
    private boolean stopByUser = false;

    private MediaPlayer mediaPlayer;
    private boolean mediaEnabled;
    private boolean vibrateEnabled;
    private Vibrator vibrator;
    private MemtaskRepositoryBase mRepository;
    private Task task;
    private static final long TWO_MINUTES = 1000 * 60 * 2;
    Ringtone r;

    @Override
    public void onCreate() {
        super.onCreate();


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String taskID = intent.getStringExtra(TaskNotificationManager.ID_KEY);

        if(intent.getIntExtra(STOP_KEY, -1) == STOP_BY_USER){
            stopByUser = true;
            stopSelf();
            return START_STICKY;
        }

        // User click
        Intent notificationOpenIntent = new Intent(this, RingActivity.class);
        notificationOpenIntent.putExtra(TaskNotificationManager.ID_KEY, intent.getStringExtra(TaskNotificationManager.ID_KEY));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationOpenIntent);

        PendingIntent pendingClickIntent = stackBuilder.getPendingIntent(TaskNotificationManager.NOTIFICATION_ALARM_CLICK, PendingIntent.FLAG_UPDATE_CURRENT);


        // Notification complete button
        Intent notificationCompleteIntent = new Intent(this, AlarmBroadcastReceiver.class);
        notificationCompleteIntent.putExtra(TaskNotificationManager.ID_KEY, taskID);
        notificationCompleteIntent.putExtra(TaskNotificationManager.ID_KEY, intent.getStringExtra(TaskNotificationManager.ID_KEY));
        notificationCompleteIntent.putExtra(TaskNotificationManager.NOTIFICATION_ALARM_REQUEST_CODE, TaskNotificationManager.NOTIFICATION_ALARM_COMPLETE);
        notificationCompleteIntent.putExtra(TaskNotificationManager.MODE_KEY, TaskNotificationManager.MODE_INLINE);

        PendingIntent pendingCompleteIntent = PendingIntent.getBroadcast(this, 0, notificationCompleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action complete = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Выполнено", pendingCompleteIntent).build();

        // Notification skip button
        Intent notificationSkipIntent = new Intent(this, AlarmBroadcastReceiver.class);
        notificationSkipIntent.putExtra(TaskNotificationManager.ID_KEY, taskID);
        notificationSkipIntent.putExtra(TaskNotificationManager.ID_KEY, intent.getStringExtra(TaskNotificationManager.ID_KEY));
        notificationSkipIntent.putExtra(TaskNotificationManager.NOTIFICATION_ALARM_REQUEST_CODE, TaskNotificationManager.NOTIFICATION_ALARM_SKIP);
        notificationSkipIntent.putExtra(TaskNotificationManager.MODE_KEY, TaskNotificationManager.MODE_INLINE);

        PendingIntent pendingSkipIntent = PendingIntent.getBroadcast(this, 1, notificationSkipIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action skip = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Пропустить", pendingSkipIntent).build();

        mRepository = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());
        task = mRepository.getTaskSilently(taskID);
        mediaEnabled = task.isMediaEnabled();
        vibrateEnabled = task.isVibrate();
        if(mediaEnabled){
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                Uri uri = Uri.parse(task.getRingtonePath());
                r = RingtoneManager.getRingtone(getApplicationContext(), uri);
                r.setLooping(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, TaskNotificationManager.NOTIFICATION_ALARM_CHANNEL_ID);

        Notification notification;
        notificationBuilder = notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(task.getName())
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_EVENT)
                .addAction(complete)
                .addAction(skip)
                .setColor(getColor(R.color.main_8))
        ;
        if(task.getDescription().length() != 0){
            notificationBuilder = notificationBuilder.setContentText(task.getDescription());
        }
        notification = notificationBuilder.build();

        if(mediaEnabled || vibrateEnabled){
            if(task.isMediaEnabled()) {
                try{
                    r.play();
                } catch (Exception e){
                    e.printStackTrace();
                }
                /*try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();*/
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(task.isNotificationInProgress() == false){
                        return;
                    }
                    task = mRepository.getTaskSilently(task.getTaskId());
                    updateTaskData(task);
                    if(r != null){
                        try{
                            r.stop();
                            if(vibrator != null){
                                vibrator.cancel();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        if(task.isCompleted() != true) {
                            task.schedule(getApplicationContext());
                            task.setCompleted(false);
                            task.setExpired(true);
                            mRepository.addTaskSilently(task);
                        }
                    }
                }
            }, TWO_MINUTES);
        }

        task.setNotificationInProgress(true);
        mRepository.addTaskSilently(task);

        startForeground(task.getNotificationID(), notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mediaEnabled) {
                r.stop();
            }
            if (vibrateEnabled) {
                vibrator.cancel();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        task = mRepository.getTaskSilently(task.getTaskId());
        //updateTaskData(task);
        task.schedule(getApplicationContext());
        mRepository.addTask(task);
        if(stopByUser){
            return;
        }
    }
    private void updateTaskData(Task task){
        if(task.isNotificationInProgress()){
            if(task.markIfExpired()){
                mRepository.addTask(task);
            }
            task.setNotificationInProgress(false);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}