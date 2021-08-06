package com.example.clock.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.model.Task;

import java.util.Calendar;

public class CreateAlarmActivity extends AppCompatActivity {

    NumberPicker hoursPicker;
    NumberPicker minutesPicker;

    Task selectedNote;

    RelativeLayout repeatModeLayout;
    RelativeLayout vibrateModeLayout;
    RelativeLayout noteLayout;
    SwitchCompat vibrateSwitch;
    AlertDialog repeatModeDialog;
    String[] repeatModes;
    int selectedRepeatMode = -1;
    int resultCode = 2;
    Calendar nowCalendar;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        hoursPicker = findViewById(R.id.hours_picker_full);
        minutesPicker = findViewById(R.id.minutes_picker_full);

        nowCalendar = Calendar.getInstance();

        selectedNote = null;
        selectedNote = (Task) getIntent().getSerializableExtra("selectedNote");

        if(selectedNote == null){
            selectedNote = new Task(Calendar.getInstance(), 1);
            selectedNote.setEnabled(true);
            resultCode = 1;
        }

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        hoursPicker.setValue(selectedNote.getHourOfDay());

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setValue(selectedNote.getMinute());

        // Hours, minutes pickers
        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                nowCalendar = Calendar.getInstance();

                int selectedHour = hoursPicker.getValue();
                int selectedMinute = minutesPicker.getValue();

                Calendar selectedTimeCalendar = Calendar.getInstance();
                selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedTimeCalendar.set(Calendar.MINUTE, selectedMinute);

                selectedNote.setTimeInMillis(selectedTimeCalendar.getTimeInMillis());

                Calendar settlementCalendar = selectedTimeCalendar;

                long time_in_milliseconds_before = nowCalendar.getTimeInMillis();
                long time_in_milliseconds_after = settlementCalendar.getTimeInMillis();


                long timeBarrier = time_in_milliseconds_after - time_in_milliseconds_before;
                if(selectedNote.getRepeatMode() <= 1){
                    if(timeBarrier < 0 && Math.abs(timeBarrier) < Task.DAY){
                        settlementCalendar.add(Calendar.MILLISECOND, Task.DAY);
                        time_in_milliseconds_after = settlementCalendar.getTimeInMillis();
                    }
                    else if(timeBarrier < 0 && Math.abs(timeBarrier)
                            < Task.DAY && Math.abs(timeBarrier) < Task.WEEK){
                        settlementCalendar.add(Calendar.MILLISECOND, Task.WEEK);
                        time_in_milliseconds_after = settlementCalendar.getTimeInMillis();
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
                nowCalendar = Calendar.getInstance();
                int selectedHour = hoursPicker.getValue();
                int selectedMinute = minutesPicker.getValue();

                Calendar selectedTimeCalendar = Calendar.getInstance();
                selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedTimeCalendar.set(Calendar.MINUTE, selectedMinute);

                selectedNote.setTimeInMillis(selectedTimeCalendar.getTimeInMillis());

                long time_in_milliseconds_before = nowCalendar.getTimeInMillis();
                long time_in_milliseconds_after = selectedTimeCalendar.getTimeInMillis();


                long timeBarrier = time_in_milliseconds_after - time_in_milliseconds_before;
                if(selectedNote.getRepeatMode() <= 1){
                    if(timeBarrier < 0 && Math.abs(timeBarrier) <= Task.DAY){
                        selectedTimeCalendar.add(Calendar.MILLISECOND, Task.DAY);
                        time_in_milliseconds_after = selectedTimeCalendar.getTimeInMillis();
                    }
                    else if(timeBarrier < 0 && Math.abs(timeBarrier)
                            < Task.DAY && Math.abs(timeBarrier) <= Task.WEEK){
                        selectedTimeCalendar.add(Calendar.MILLISECOND, Task.WEEK);
                        time_in_milliseconds_after = selectedTimeCalendar.getTimeInMillis();
                    }
                }
                //DEBUG
                int changedMinute = selectedTimeCalendar.get(Calendar.MINUTE);
                int changedHour = selectedTimeCalendar.get(Calendar.HOUR_OF_DAY);
                int changedDay = selectedTimeCalendar.get(Calendar.DAY_OF_MONTH);
                //DEBUG

                double difference_in_seconds = (time_in_milliseconds_after - time_in_milliseconds_before)
                                                / 1000;
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


        repeatModes = getResources().getStringArray(R.array.repeat_modes);
        repeatModeLayout = (RelativeLayout) View.inflate(this, R.layout.bottom_list_button_field, null);
        repeatModeLayout.setId(1001);
        TextView textView = (TextView) repeatModeLayout.findViewWithTag("custom_button_text2");
        textView.setText(repeatModes[selectedNote.getRepeatMode()]);
        LinearLayout propertiesLayout = (LinearLayout) findViewById(R.id.properties_layout_full);
        repeatModeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        repeatModeLayout.setBackgroundColor(getColor(R.color.light_grey));
                        return true;
                    case MotionEvent.ACTION_UP:
                        repeatModeLayout.setBackgroundColor(getColor(R.color.white));
                        showRepeatModeSelectDialog();
                        return true;
                }
                return false;
            }
        });
        propertiesLayout.addView(repeatModeLayout);

        vibrateModeLayout = (RelativeLayout) View.inflate(this, R.layout.switch_field, null);
        vibrateModeLayout.setId(1002);
        vibrateSwitch = vibrateModeLayout.findViewWithTag("switch");
        vibrateSwitch.setChecked(selectedNote.isVibrate());

        vibrateSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchState = vibrateSwitch.isChecked();
                selectedNote.setVibrate(switchState);
            }
        });
        vibrateModeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        vibrateModeLayout.setBackgroundColor(getColor(R.color.light_grey));
                        return true;
                    case MotionEvent.ACTION_UP:
                        vibrateModeLayout.setBackgroundColor(getColor(R.color.white));
                        boolean switchState = vibrateSwitch.isChecked();
                        selectedNote.setVibrate(switchState);
                        if(switchState){
                            vibrateSwitch.setChecked(false);
                        }
                        else{
                            vibrateSwitch.setChecked(true);
                        }
                        return true;
                }
                return false;
            }
        });
        propertiesLayout.addView(vibrateModeLayout);

        noteLayout = (RelativeLayout) View.inflate(this, R.layout.bottom_list_button_field, null);
        noteLayout.setId(1003);
        textView = (TextView) noteLayout.findViewWithTag("custom_button_text1");
        textView.setText("Brief note");
        noteLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        noteLayout.setBackgroundColor(getColor(R.color.light_grey));
                        return true;
                    case MotionEvent.ACTION_UP:
                        noteLayout.setBackgroundColor(getColor(R.color.white));
                        showUserNoteDialog();
                        return true;
                }
                return false;
            }
        });
        propertiesLayout.addView(noteLayout);
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

    private void showRepeatModeSelectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomButtonTheme);
        builder.setTitle("");


        builder.setItems(repeatModes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedRepeatMode = which;
                TextView textView = (TextView) repeatModeLayout.findViewWithTag("custom_button_text2");
                textView.setText(repeatModes[which]);
                selectedNote.setRepeatMode(which);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM;
        dialog.show();
    }
    private void showUserNoteDialog(){
        Intent createNoteWindow = new Intent(this, InputNoteActivity.class);
        String currentNote = selectedNote.getDescription();

        createNoteWindow.putExtra("note", currentNote);
        startActivityForResult(createNoteWindow, 1);
    }

    public void onCancel(View view) {
        resultCode = 0;
        Intent resultIntent = new Intent();
        setResult(resultCode, resultIntent);
        finish();
    }
    public void onSave(View view) {
        resultCode = 1;
        Intent resultIntent = new Intent();
        setResult(resultCode, resultIntent);
        App.getInstance().insertWithReplace(selectedNote);
        finish();
    }
    @Override
    public void onBackPressed() {
        resultCode = 0;
        Intent resultIntent = new Intent();
        setResult(resultCode, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        boolean  success = false;
        if (resultCode == 1){
            String new_note = (String) data.getStringExtra("note");
            selectedNote.setDescription(new_note);
        }
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}