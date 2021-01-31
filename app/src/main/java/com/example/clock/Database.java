package com.example.clock;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Alarm.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract AlarmDao alarmDao();
}
