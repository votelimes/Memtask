package com.example.clock;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {AlarmNote.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract AlarmNoteDao alarmNoteDao();
}
