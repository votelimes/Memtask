package com.example.clock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerFull extends AppCompatActivity {

    NumberPicker hoursPicker;
    NumberPicker minutesPicker;

    AlarmNote alarmCalendar;
    AlarmNote selectedNote;

    RelativeLayout repeatModeLayout;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker_full);

        hoursPicker = findViewById(R.id.hours_picker_full);
        minutesPicker = findViewById(R.id.minutes_picker_full);

        alarmCalendar = new AlarmNote(Calendar.getInstance(), 1);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        hoursPicker.setValue(alarmCalendar.getHourOfDay());

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setValue(alarmCalendar.getMinute());

        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //!!!DEBUG!!! REMOVE UNTIL RELEASE
                alarmCalendar = new AlarmNote(Calendar.getInstance(), 0);
                //!!!DEBUG!!! REMOVE UNTIL RELEASE

                Calendar new_time_calendar = Calendar.getInstance();
                new_time_calendar.set(Calendar.HOUR_OF_DAY, newVal);
                new_time_calendar.set(Calendar.MINUTE, minutesPicker.getValue());


                long time_in_milliseconds_before = alarmCalendar.getTimeInMillis();
                long time_in_milliseconds_after = new_time_calendar.getTimeInMillis();

                //86400000; +1 day, 604800000; +1 week
                if(alarmCalendar.getRepeatMode() <= 1){
                    if(time_in_milliseconds_after < time_in_milliseconds_before &&
                            time_in_milliseconds_before - time_in_milliseconds_after < 86400000){
                        new_time_calendar.add(Calendar.DAY_OF_YEAR, 1);
                        time_in_milliseconds_after = new_time_calendar.getTimeInMillis();
                    }
                }

                double difference_in_minutes = ((time_in_milliseconds_after - time_in_milliseconds_before)
                        / 1000) / 60;
                double difference_in_hours = (((time_in_milliseconds_after - time_in_milliseconds_before)
                        / 1000) / 60) / 60;
                double difference_in_days = (((((time_in_milliseconds_after - time_in_milliseconds_before)
                        / 1000) / 60) / 60) / 24);

                difference_in_hours -= difference_in_days*24;
                difference_in_minutes -= difference_in_hours*60;

                setBeforeAlarmText((long)difference_in_days, (long)difference_in_hours,
                                                            (long)difference_in_minutes);
            }
        });
        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //!!!DEBUG!!! REMOVE UNTIL RELEASE
                alarmCalendar = new AlarmNote(Calendar.getInstance(), 0);
                //!!!DEBUG!!! REMOVE UNTIL RELEASE

                Calendar new_time_calendar = Calendar.getInstance();
                new_time_calendar.set(Calendar.MINUTE, newVal);
                new_time_calendar.set(Calendar.HOUR_OF_DAY, hoursPicker.getValue());

                long time_in_milliseconds_before = alarmCalendar.getTimeInMillis();
                long time_in_milliseconds_after = new_time_calendar.getTimeInMillis();

                //86400000; +1 day, 604800000; +1 week
                if(alarmCalendar.getRepeatMode() <= 1){
                    if(time_in_milliseconds_after < time_in_milliseconds_before &&
                            time_in_milliseconds_before - time_in_milliseconds_after < 86400000){
                        new_time_calendar.add(Calendar.DAY_OF_YEAR, 1);
                        time_in_milliseconds_after = new_time_calendar.getTimeInMillis();
                    }
                }

                double difference_in_minutes = ((time_in_milliseconds_after - time_in_milliseconds_before)
                        / 1000) / 60;
                double difference_in_hours = (((time_in_milliseconds_after - time_in_milliseconds_before)
                        / 1000) / 60) / 60;
                double difference_in_days = (((((time_in_milliseconds_after - time_in_milliseconds_before)
                        / 1000) / 60) / 60) / 24);

                difference_in_hours -= difference_in_days*24;
                difference_in_minutes -= difference_in_hours*60;

                setBeforeAlarmText((long)difference_in_days, (long)difference_in_hours,
                        (long)difference_in_minutes);
            }
        });

        selectedNote = null;
        selectedNote = (AlarmNote) getIntent().getSerializableExtra("selectedNote");

        repeatModeLayout = (RelativeLayout) View.inflate(this, R.layout.custom_button1, null);
        repeatModeLayout.setId(1001);
        LinearLayout propertiesLayout = (LinearLayout) findViewById(R.id.properties_layout_full);
        propertiesLayout.addView(repeatModeLayout);
        repeatModeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        repeatModeLayout.setBackgroundColor(getColor(R.color.light_grey));
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        repeatModeLayout.setBackgroundColor(getColor(R.color.white));
                        return true;
                }
                return false;
            }
        });

    }
    protected void setBeforeAlarmText(long days, long hours, long minutes){
        Calendar calendar = Calendar.getInstance();

        TextView before_alarm_text = findViewById(R.id.before_alarm_full);
        String text = "In ";

        if (days != 0){
            if(days == 1){
                text += " 1 day ";
            }
            else {
                text += String.valueOf(days) + " days ";
            }
        }
        if (hours != 0){
            if(hours == 1){
                text +=  " 1 hour ";
            }
            else {
                text += String.valueOf(hours) + " hours ";
            }
        }
        if ((days == 0 && hours != 0) || (days == 0 && minutes > 0)){
            if(minutes == 1){
                text +=  " 1 minute";
            }
            else {
                text += String.valueOf(minutes) + " minutes";
            }
        }
        else if(days == 0 && hours == 0 && minutes == 0){
            text = "In one day";
        }
        else if (days == 0){
            text = "In less than one minute";
        }
        before_alarm_text.setText(text);
    }

    public void onSave(View view) {

    }
    private int showRepeatModeSelectDialog(){
        int selectedItem = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose repeat mode");

        String[] repeatModes = getResources().getStringArray(R.array.repeat_modes);


        return 0;
    }
}