package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimePickerFull extends AppCompatActivity {

    NumberPicker hoursPicker;
    NumberPicker minutesPicker;
    Calendar calendar_now;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker_full);

        hoursPicker = findViewById(R.id.hours_picker_full);
        minutesPicker = findViewById(R.id.minutes_picker_full);

        calendar_now = Calendar.getInstance();
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        hoursPicker.setValue(calendar_now.get(Calendar.HOUR));

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setValue(calendar_now.get(Calendar.MINUTE));

        Calendar test_calendar = Calendar.getInstance();
        //test_calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        test_calendar.set(Calendar.HOUR_OF_DAY, 9);
        test_calendar.set(Calendar.MINUTE, 0);
        setBeforeAlarmText(getTimeDifference(test_calendar.getTimeInMillis()));
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
        if ((days == 0 && hours != 0) || (days == 0 && minutes > 1)){
            text += String.valueOf(minutes) + " minutes";
        }
        else if (days == 0){
            text = "Alarm in one minute";
        }
        before_alarm_text.setText(text);
    }
    protected void getSelectedTime(){
        calendar_now = Calendar.getInstance();

        Calendar calendar_selected = Calendar.getInstance();
        calendar_selected.clear();
        //calendar_selected.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        calendar_selected.set(Calendar.HOUR_OF_DAY, hoursPicker.getValue());
        calendar_selected.set(Calendar.MINUTE, minutesPicker.getValue());
    }
    protected List<Integer> getTimeDifference(long time_in_milliseconds_after){
        Calendar difference_calendar = Calendar.getInstance();
        List<Integer> time_difference = new ArrayList<Integer>();

        long time_in_milliseconds_before = difference_calendar.getTimeInMillis();

        // add 1 week in milliseconds if calendar stepped back on 1 week
        if(time_in_milliseconds_after < time_in_milliseconds_before){
            time_in_milliseconds_after += 604800000;
        }

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