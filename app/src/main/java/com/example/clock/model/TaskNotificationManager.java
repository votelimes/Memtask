package com.example.clock.model;

public class TaskNotificationManager {
    public static final int MODE_INLINE = 4513594;
    public static final int MODE_GENERAL = 4513595;
    public static final String MODE_KEY = "NOTIFICATION_TYPE";
    public static final String ID_KEY = "ACTIVITY_ID";

    public static final int REQUEST_CODE_BASE = -1;
    public static final long SNOOZE_TIME = 1000 * 60 * 2;
    public static final long STD_ADJUSTMENT_TIME = SNOOZE_TIME;

    public static final String NOTIFICATION_ALARM_CHANNEL_ID = "MEMTASK_CHANNEL_INLINE";
    public static final String NOTIFICATION_ALARM_CHANNEL_NAME = "Уведомления со временем";
    public static final String NOTIFICATION_ALARM_CHANNEL_DESCRIPTION = "Временные уведомления по задачам, с вибрацией и/или звуком";

    public static final String NOTIFICATION_GENERAL_CHANNEL_ID = "MEMTASK_CHANNEL_GENERAL";
    public static final String NOTIFICATION_GENERAL_CHANNEL_NAME = "Уведомления о активности";
    public static final String NOTIFICATION_GENERAL_CHANNEL_DESCRIPTION = "Общие уведомления, информирующие о прогрессе по задачам или проекте, статистике";

    public static final String NOTIFICATION_ALARM_REQUEST_CODE = "REQUEST_CODE";
    public static final int NOTIFICATION_ALARM_CLICK = 500;
    public static final int NOTIFICATION_ALARM_COMPLETE = 501;
    public static final int NOTIFICATION_ALARM_POSTPONE = 502;
    public static final int NOTIFICATION_ALARM_SKIP = 503;
}
