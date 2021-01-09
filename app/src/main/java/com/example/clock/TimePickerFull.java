package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerFull extends AppCompatActivity {

    NumberPicker hoursPicker;
    NumberPicker minutesPicker;
    AlarmNote alarmCalendar;
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
                alarmCalendar.setHourOfDay(newVal);
                setBeforeAlarmText(getTimeDifference(alarmCalendar.getTimeInMillis(),
                                                    alarmCalendar.getRepeatMode()));
            }
        });
        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                alarmCalendar.setMinute(newVal);
                setBeforeAlarmText(getTimeDifference(alarmCalendar.getTimeInMillis(),
                                                    alarmCalendar.getRepeatMode()));
            }
        });
    }
    protected void setBeforeAlarmText(List<Integer> time_difference){
        Calendar calendar = Calendar.getInstance();

        TextView before_alarm_text = findViewById(R.id.before_alarm_full);
        String text = "Before alarm ";
        int days = time_difference.get(0);
        int hours = time_difference.get(1);
        int minutes = time_difference.get(2);

        if (days != 0){
            text += String.valueOf(days) + " days ";
        }
        if (hours != 0){
            text += String.valueOf(hours) + " hours ";
        }
        if ((days == 0 && hours != 0) || (days == 0 && minutes > 0)){
            text += String.valueOf(minutes) + " minutes";
        }
        else if (days == 0){
            text = "Alarm in one minute";
        }
        else if(days == 0 && hours == 0 && minutes == 0){
            text = "";
        }
        before_alarm_text.setText(text);
    }
    protected List<Integer> getTimeDifference(long time_in_milliseconds_after, int repeatMode){
        Calendar difference_calendar = Calendar.getInstance();
        List<Integer> time_difference = new ArrayList<Integer>();

        long time_in_milliseconds_before = difference_calendar.getTimeInMillis();

        // add 1 week in milliseconds if calendar stepped back on 1 week
        long difference = Math.abs(time_in_milliseconds_after - time_in_milliseconds_before);

        if(repeatMode == 1 && difference < 86400000 && difference > 604800000){
            time_in_milliseconds_after += 604800000; // +1 day
        }
        //time_in_milliseconds_after += 604800000; +1 week

        double difference_in_minutes = ((time_in_milliseconds_after - time_in_milliseconds_before)
                            / 1000) / 60;
        double difference_in_hours = (((time_in_milliseconds_after - time_in_milliseconds_before)
                / 1000) / 60) / 60;
        double difference_in_days = (((((time_in_milliseconds_after - time_in_milliseconds_before)
                / 1000) / 60) / 60) / 24);

        time_difference.add((int) difference_in_hours/24); // Days
        time_difference.add((int) (difference_in_hours - difference_in_days * 24)); // Hours
        time_difference.add((int) (difference_in_minutes - difference_in_hours * 60)); // Minutes

        return time_difference;
    }
}