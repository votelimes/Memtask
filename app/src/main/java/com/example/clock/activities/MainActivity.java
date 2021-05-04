package com.example.clock.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.data.Alarm;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;

    private final String[] dayNames = {"NULL", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private LinearLayout userNoteLayout;
    private Context mContext;
    private int lastClickedUserNoteIndex = -1;
    private LiveData<List<Alarm>> alarmsData;
    private boolean ignoreUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNoteLayout = findViewById(R.id.userNoteTopLayout);

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(600);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mContext = this;

        alarmsData = App.getInstance().getAlarmsLiveData();
        alarmsData.observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                if(!isUpdateDisabled()) {
                    clearNoteLayout();
                    printCloseNotes(alarms);
                }
                else{
                    enableUpdate();
                }
            }
        });
    }

    public void onAddClock(View view) {
        Intent clock_window = new Intent(this, CreateAlarmActivity.class);
        Alarm selectedNote = null;
        if(lastClickedUserNoteIndex != -1) {
            for (Alarm note : App.getInstance().getAlarmsLiveData().getValue()) {
                if (note.alarmId == lastClickedUserNoteIndex) {
                    selectedNote = note;
                    break;
                }
            }
        }
        clock_window.putExtra("selectedNote", selectedNote);
        startActivityForResult(clock_window, 1);
    }

    private void addNoteToLayout(Alarm alarm){
        LinearLayout newNoteLayout = (LinearLayout) View.inflate(this, R.layout.user_note, null);
        newNoteLayout.setId((int) alarm.alarmId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        params.setMargins(0, 30, 0, 30);

        newNoteLayout.setLayoutParams(params);
        TextView time = (TextView) newNoteLayout.findViewWithTag("time_day_of_week")
                .findViewWithTag("time");
        time.setText(timeInMillisToTime(alarm.getTimeInMillis()));

        TextView day_of_week = (TextView) newNoteLayout.findViewWithTag("time_day_of_week")
                .findViewWithTag("day_of_week");
        day_of_week.setText(dayNames[alarm.getDayOfWeek()]);

        TextView userNote = (TextView) newNoteLayout.findViewWithTag("text");
        userNote.setText(alarm.getNote());

        SwitchCompat enableSwitch = newNoteLayout.
                findViewWithTag("switch_layout").findViewWithTag("switch");

        userNoteLayout.addView(newNoteLayout);

        newNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickedUserNoteIndex == v.getId()) {
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), App.getColor("mainTheme5"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastClickedUserNoteIndex = -1;
                } else {
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), App.getColor("mainTheme5"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        changeStrokeColor(v, App.getColor("mainTheme4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastClickedUserNoteIndex = v.getId();
                }
            }
        });

        newNoteLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

                dialog.setMessage("Are you sure want to delete this alarm?");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userNoteLayout.removeView(v);

                            int viewId = v.getId();
                            //Alarm removableNote = App.getInstance().getById(viewId);
                            App.getInstance().removeById(viewId);
                            Toast.makeText(mContext, "Successfully deleted", Toast.LENGTH_SHORT).show();

                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return true;
            }
        });

        enableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableUpdate();
                SwitchCompat activeSwitch = (SwitchCompat) v;
                LinearLayout currentAlarmLayout = (LinearLayout) activeSwitch.getParent().getParent();
                long currentAlarmId = currentAlarmLayout.getId();

                Alarm currentAlarm = App.getInstance().getById(currentAlarmId);
                boolean previousState = currentAlarm.isEnabled();
                currentAlarm.setEnabled(activeSwitch.isChecked());

                long currentTimeInMillis = System.currentTimeMillis();
                if(currentAlarm.getTimeInMillis() > currentTimeInMillis + 3000) {
                    if (previousState == false) {
                        currentAlarm.schedule(getApplicationContext());
                        currentAlarm.setEnabled(true);
                    }
                    if (previousState == true){
                        currentAlarm.cancelAlarm(getApplicationContext());
                        currentAlarm.setEnabled(false);
                    }
                }
                App.getInstance().update(currentAlarm);
            }
        });

        enableSwitch.setChecked(alarm.isEnabled());
    }
    private void changeStrokeColor(View v, int color){
        Drawable background = (Drawable) v.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.mutate();
        gradientDrawable.setStroke(3, color);
    }
    private void printCloseNotes(List<Alarm> alarms){
        long timeInMillis = System.currentTimeMillis();

        for(Alarm note : alarms){
            long closeTimeBarrier = Math.abs(note.getTimeInMillis() - timeInMillis);
            // Debug. Remove true statement.
            if(closeTimeBarrier <= Alarm.DAY || true){
                addNoteToLayout(note);
            }
        }
    }
    private void clearNoteLayout(){
        userNoteLayout.removeAllViews();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 1 || resultCode != 2){
            try{
                changeStrokeColor(findViewById(lastClickedUserNoteIndex), App.getColor("mainTheme4"));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        lastClickedUserNoteIndex = -1;
    }

    // Utility methods
    public int pxToDp(Context context, int px) {
        return  ((int) (px / context.getResources().getDisplayMetrics().density));
    }
    public String timeInMillisToTime(long timeInMillis){
        Date time = new Date(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(time);
    }
    public void disableUpdate(){
        this.ignoreUpdate = true;
    }
    public void enableUpdate(){
        this.ignoreUpdate = false;
    }
    public boolean isUpdateDisabled(){
        return this.ignoreUpdate;
    }
    // Debug methods, have to be removed before release
}