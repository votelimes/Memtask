package com.example.clock;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;
    private List<AlarmNote> alarmNoteList;
    private AlarmNoteDao database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmNoteList = new ArrayList<AlarmNote>();

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(600);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        database = App.getInstance().getDatabase().alarmNoteDao();
    }

    public void onAddClock(View view) {
        Intent clock_window = new Intent(this, TimePickerFull.class);
        startActivity(clock_window);
    }

    private void debugWriteToDB(){
        AlarmNote alarmNote = new AlarmNote(Calendar.getInstance(), 0, "TEST0");
        database.insert(alarmNote);
    }

    private void debugReadFromDB(){
        alarmNoteList = database.getAll();

        //Log.d("DB_TESTING", "READ_START");

        Log.d("DB_TESTING", "READ_END");
    }
}