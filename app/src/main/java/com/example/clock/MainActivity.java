package com.example.clock;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(600);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void onAddClock(View view) {
        Intent clock_window = new Intent(this, TimePickerFull.class);
        startActivity(clock_window);
    }
}