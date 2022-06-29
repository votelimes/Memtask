package com.example.clock.model;

import android.content.Context;

import com.example.clock.app.App;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    public static final long MILLIS_IN_HOUR = 1000 * 60 * 60;

    public static final int GENERAL_NOTIFICATION_MAIN_DIFFERENCE = 4;

    public static void scheduleGeneralNotifications(Context context){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startInterval;
        LocalDateTime endInterval;

        List<TaskNotificationData> activeTasks = App.getDatabase().taskDao().getTasksNotificationData(today.toEpochSecond(ZoneOffset.UTC)*1000);
        List<DayLoad> days;

        if(today.getHour() < 10){
            startInterval = today;
        }
        else{
            startInterval = today.plusDays(1);
        }
        endInterval = startInterval;
        for (int i = 0; i < activeTasks.size(); i++){
            if(activeTasks.get(i).task.getEndTime() > (endInterval.toEpochSecond(ZoneOffset.UTC) * 1000)){
                endInterval = LocalDateTime.ofEpochSecond(activeTasks.get(i).task.getEndTime() / 1000, 0, ZoneOffset.UTC);
            }
        }

        days = new ArrayList<DayLoad>((int) ChronoUnit.DAYS.between(startInterval, endInterval));
        for (int i = 0; i < (int) ChronoUnit.DAYS.between(startInterval, endInterval) + 1; i++){
            days.add(new DayLoad(today.toLocalDate().plusDays(i)));
        }

        // Расчет нагрузки
        for(int i = 0; i < activeTasks.size(); i++){
            Task task = activeTasks.get(i).task;
            LocalDateTime now = today;
            LocalDateTime start = LocalDateTime.ofEpochSecond(task.getStartTime()/1000, 0, ZoneOffset.UTC);
            LocalDateTime end = LocalDateTime.ofEpochSecond(task.getEndTime()/1000, 0, ZoneOffset.UTC);

            // 15 ()

            int dayStartIndex = (int) ChronoUnit.DAYS.between(today, start);
            if(start.toLocalDate().isBefore(today.toLocalDate())){
                dayStartIndex = 0;
            }
            int dayEndIndex = (int) ChronoUnit.DAYS.between(now, end);

            int stepsMax = (task.getDuration() - task.getProgress());
            int stepNow = dayStartIndex;
            int stepSize = (int) task.getStep();
            //long daysRange = ChronoUnit.DAYS.between(now, end);

            while(stepNow < stepsMax){
                if(stepNow >= days.size()){
                    break;
                }
                if(stepNow + stepSize >= stepsMax){
                    stepSize = stepsMax - stepNow;
                }
                if(stepNow == 0 && dayEndIndex / 2 > stepsMax){
                    stepNow += stepSize;
                    stepsMax += stepSize;
                    continue;
                }
                boolean isAdded = days.get(stepNow).addOnTop(stepSize, false);
                if(!isAdded){
                    int currentDay = stepNow;
                    boolean outOfRange = false;

                    // Пустые дни вправо
                    while(!isAdded){
                        if(currentDay > dayEndIndex){
                            outOfRange = true;
                            break;
                        }
                        isAdded = days.get(currentDay).addOnTop(stepSize, false);
                        if(isAdded){
                            outOfRange = false;
                            break;
                        }
                        currentDay++;
                    }
                    // Пустые дни влево
                    if(outOfRange || !isAdded){
                        currentDay = stepNow;
                        while(!isAdded){
                            if(currentDay < dayStartIndex){
                                outOfRange = true;
                                break;
                            }
                            isAdded = days.get(currentDay).addOnTop(stepSize, false);
                            if(isAdded){
                                outOfRange = false;
                                break;
                            }
                            currentDay--;
                        }
                    }

                    // Любые дни вправо
                    if(outOfRange || !isAdded){
                        currentDay = stepNow;
                        outOfRange = false;
                        while(!isAdded){
                            if(currentDay > dayEndIndex){
                                outOfRange = true;
                                break;
                            }
                            isAdded = days.get(currentDay).addOnTop(stepSize, true);
                            if(isAdded){
                                outOfRange = false;
                                break;
                            }
                            currentDay++;
                        }
                    }
                    // Любые дни влево
                    if(outOfRange || !isAdded){
                        currentDay = stepNow;
                        while(!isAdded){
                            if(currentDay < dayStartIndex){
                                outOfRange = true;
                                break;
                            }
                            isAdded = days.get(currentDay).addOnTop(stepSize, true);
                            if(isAdded){
                                outOfRange = false;
                                break;
                            }
                            currentDay--;
                        }
                    }
                }

                if(stepNow == 0){
                    long notificationMillis = days.get(stepNow).getLastIntervalMillis(stepSize);
                    task.scheduleGeneral(context, notificationMillis);
                }
                stepNow+=stepSize;
            }
        }
    }

    private static class DayLoad {
        public int hour[] = new int[24];

        private final int START_WORK_DAY_HOUR = 9;
        private final int END_WORK_DAY_HOUR = 22;
        private LocalDate date;

        public DayLoad(LocalDate date){
            this.date = date;
        }

        public boolean addLoad(int startHour, int count){
            int index = startHour;
            int current = 0;

            while(current < count){
                hour[index]++;
                index++;
                current++;
                if(index >= END_WORK_DAY_HOUR){
                    return false;
                }
            }
            return true;
        }
        public boolean addOnTop(int count, boolean hardInsert){
            int i = START_WORK_DAY_HOUR;
            if(hour[START_WORK_DAY_HOUR] != 0 && !hardInsert){
                return false;
            }
            while (hour[i] != 0) {
                if (i >= END_WORK_DAY_HOUR) {
                    return false;
                }
                i++;
            }
            return addLoad(i, count);
        }
        public boolean isBefore(LocalDate date){
            return this.date.isBefore(date);
        }
        public boolean isAfter(LocalDate date){
            return this.date.isAfter(date);
        }

        public int getLastIntervalHour(int count){
            int c = 0;
            int hourOfDay = 0;
            for(int i = 23; i > -1; i--){
                if(hour[i] != 0 && c < count){
                    hourOfDay = i + 1;
                    c++;
                }
            }
            return hourOfDay;
        }
        public long getLastIntervalMillis(int count){
            return date.atStartOfDay().withHour(getLastIntervalHour(count)).toEpochSecond(ZoneOffset.UTC) * 1000;
        }
    }
}
