package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.NumberPicker;

public class TimePickerFull extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker_full);

        String[] minutes = new String[60];
        String[] hours = new String[24];
        for(int i = 0; i < 60; i++){
            if(i < 24){
                hours[i] = String.valueOf(i + 1);
            }
            minutes[i] = String.valueOf(i + 1);
        }

        NumberPicker hoursPicker = findViewById(R.id.hours_picker_full);
        NumberPicker minutesPicker = findViewById(R.id.minutes_picker_full);

        hoursPicker.setDisplayedValues(hours);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);


        minutesPicker.setDisplayedValues(minutes);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
    }
}