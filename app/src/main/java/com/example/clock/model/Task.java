package com.example.clock.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity(tableName = "task_table")
public class Task extends UserCaseBase {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "task_ID")
    private String taskId;


    protected boolean vibrate;

    // 0: Однократно, 1: Каждый день, 2: По будням, 3: Выбрать дни 4: Ежемесячно
    protected int repeatMode;


    protected boolean notifyEnabled;
    protected long mNotificationStartMillis;

    // 1, 2, 3, 4, 5, 6, 7
    protected boolean sunday;
    protected boolean monday;
    protected boolean tuesday;
    protected boolean wednesday;
    protected boolean thursday;
    protected boolean friday;
    protected boolean saturday;

    protected boolean recurring;
    protected boolean enabled;
    protected boolean started;

    protected String mParentID;

    public Task(String name, String description, long catID){
        super();
        mParentID = "";
        taskId = generateUUID();
        mName = name;
        mDescription = description;
        categoryId = catID;
        timeCreated = GregorianCalendar.getInstance().getTimeInMillis();
        timeChanged = GregorianCalendar.getInstance().getTimeInMillis();
    }

    public Task(long notificationStartMillis, int repeatMode, String name, String description,
                String categoryName, long categoryId){

        this.taskId = generateUUID();
        this.mNotificationStartMillis = notificationStartMillis;

        this.repeatMode = repeatMode;
        this.mName = name;
        this.mDescription = description;
        this.mCategoryName = categoryName;
        this.vibrate = true;
        this.categoryId = categoryId;
        this.mParentID = "";
        /*this.startTime = 0;
        this.endTime = 0;*/

        switch (repeatMode) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
        }
    }

    @Ignore
    public Task(Calendar calendar, int repeatMode, long categoryId){

        this.taskId = generateUUID();
        this.mNotificationStartMillis = calendar.getTimeInMillis();
        this.mDescription = "";
        this.repeatMode = repeatMode;
        this.vibrate = true;
        this.categoryId = categoryId;
        this.mParentID = "";

        switch (repeatMode) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
        }
    }

    @Ignore
    public Task(int repeatMode, long timeInMillis, String description, long categoryId){
        this.taskId = generateUUID();

        this.repeatMode = repeatMode;
        this.mNotificationStartMillis = timeInMillis;
        this.mDescription = description;
        this.vibrate = true;
        this.categoryId = categoryId;
        this.mParentID = "";


        switch (repeatMode) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
        }
    }

    @Ignore
    public Task(int repeatMode, boolean started, boolean recurring, long timeInMillis, String description,
                boolean sunday, boolean monday, boolean tuesday, boolean wednesday,
                boolean thursday, boolean friday, boolean saturday, long categoryId){
        this.taskId = generateUUID();

        this.repeatMode = repeatMode;
        this.started = started;
        this.recurring = recurring;
        this.mNotificationStartMillis = timeInMillis;
        this.mDescription = description;
        this.vibrate = true;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.categoryId = categoryId;

        this.mParentID = "";

        switch (repeatMode) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
        }
    }

    /*public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("RECURRING", recurring);
        intent.putExtra("MONDAY", monday);
        intent.putExtra("TUESDAY", tuesday);
        intent.putExtra("WEDNESDAY", wednesday);
        intent.putExtra("THURSDAY", thursday);
        intent.putExtra("FRIDAY", friday);
        intent.putExtra("SATURDAY", saturday);
        intent.putExtra("SUNDAY", sunday);

        intent.putExtra("TITLE", description);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, (int) taskId, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getTimeInMillis());

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!recurring) {
            String toastText = null;
            try {
                toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", description,
                        toDay(calendar.get(Calendar.DAY_OF_WEEK)),
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        }
        else {
            String toastText = String.format("Recurring Alarm %s scheduled for %s at %02d:%02d", description, getRecurringDaysText(),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), taskId);

            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }

        this.started = true;
    }*/

    /*public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, (int) taskId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.timeInMillis);

        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), taskId);

        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }*/
    public long getNotificationStartMillis(){
        return mNotificationStartMillis;
    }
    public void setNotificationStartMillis(long millis){
        this.mNotificationStartMillis = millis;
    }
    public void setYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.YEAR, year);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setMonth(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.MONTH, month);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setHourOfDay(int hourOfDay){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setMinute(int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        calendar.set(Calendar.MINUTE, minute);

        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setTaskId(String id){
        this.taskId = id;
    }
    public void setRepeatMode(int repeatMode){
        this.repeatMode = repeatMode;
    }

    public void setVibrate(boolean vibrate){
        this.vibrate = vibrate;
    }

    public void setAlarmTime(long timeInMillis){
        this.mNotificationStartMillis = timeInMillis;
    }
    public void setAlarmTime(String time){
        Calendar calendar = GregorianCalendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            calendar.setTime(sdf.parse(time));
            mNotificationStartMillis = calendar.getTimeInMillis();
        } catch (ParseException e){
            Log.e("TASK ALARM TIME SETUP ERROR: ", e.getMessage());
        }

    }


    public void setDayOfWeek(int dayOfWeek){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        this.mNotificationStartMillis = calendar.getTimeInMillis();
    }
    public void setActiveDayOfWeek(int dayOfWeek, boolean state){
        switch (dayOfWeek){
            case 1:
                this.monday = state;
                break;
            case 2:
                this.tuesday = state;
                break;
            case 3:
                this.wednesday = state;
                break;
            case 4:
                this.thursday = state;
                break;
            case 5:
                this.friday = state;
                break;
            case 6:
                this.saturday = state;
                break;
            case 7:
                this.sunday = state;
                break;
        }
        if(dayOfWeek == 0){
            this.sunday = state;
            this.monday = state;
            this.tuesday = state;
            this.wednesday = state;
            this.thursday = state;
            this.friday = state;
            this.saturday = state;
        }
    }
    private void setWeekdays(boolean state){
        setMonday(state);
        setTuesday(state);
        setWednesday(state);
        setThursday(state);
        setFriday(state);
    }
    private void setDaysOfWeek(boolean state){
        setMonday(state);
        setTuesday(state);
        setWednesday(state);
        setThursday(state);
        setFriday(state);
        setSaturday(state);
        setSunday(state);
    }
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public int getRepeatMode(){
        return this.repeatMode;
    }

    public int getYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.MONTH);
    }
    public int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.DAY_OF_WEEK);
    }
    public boolean getActiveDayOfWeek(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return this.sunday;
            case 2:
                return this.monday;
            case 3:
                return this.tuesday;
            case 4:
                return this.wednesday;
            case 5:
                return this.thursday;
            case 6:
                return this.friday;
            case 7:
                return this.saturday;
            default:
                return false;
        }
    }
    public int getHourOfDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getMinute(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mNotificationStartMillis);

        return calendar.get(Calendar.MINUTE);
    }
    public String getTaskId() {
        return taskId;
    }

    public boolean isEnabled(){
        return  this.enabled;
    }
    public boolean isVibrate(){
        return this.vibrate;
    }
    public boolean isRecurring() {
        return this.recurring;
    }
    public boolean isMonday() {
        return this.monday;
    }
    public boolean isTuesday() {
        return this.tuesday;
    }
    public boolean isWednesday() {
        return this.wednesday;
    }
    public boolean isThursday() {
        return this.thursday;
    }
    public boolean isFriday() {
        return this.friday;
    }
    public boolean isSaturday() {
        return this.saturday;
    }
    public boolean isSunday() {
        return this.sunday;
    }
    public boolean isStarted() {
        return this.started;
    }

    protected String toDay(int dayOfWeek){
        switch (dayOfWeek){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }
        return "NULL";
    }
    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "Mo ";
        }
        if (tuesday) {
            days += "Tu ";
        }
        if (wednesday) {
            days += "We ";
        }
        if (thursday) {
            days += "Th ";
        }
        if (friday) {
            days += "Fr ";
        }
        if (saturday) {
            days += "Sa ";
        }
        if (sunday) {
            days += "Su ";
        }

        return days;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getParentID() {
        return mParentID;
    }

    public void setParentID(String parentID) {
        this.mParentID = parentID;
    }

    public boolean isNotifyEnabled() {
        return notifyEnabled;
    }

    public void setNotifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
    }
}
