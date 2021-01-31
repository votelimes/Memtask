package com.example.clock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;
    private List<Alarm> alarmList;
    private AlarmDao database;
    private final String[] dayNames = {"NULL", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private LinearLayout userNoteLayout;
    private Context mContext;
    private int lastClickedUserNoteIndex = -1;
    private Calendar chosenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmList = new ArrayList<Alarm>();

        userNoteLayout = findViewById(R.id.userNoteTopLayout);

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(600);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mContext = this;

        database = App.getInstance().getDatabase().alarmDao();
        alarmList = database.getAll();

        for (Alarm element : alarmList){
            addNoteToLayout(element);
        }

        chosenTime = Calendar.getInstance();
    }

    public void onAddClock(View view) {
        Intent clock_window = new Intent(this, CreateAlarmActivity.class);
        Alarm selectedNote = null;
        if(lastClickedUserNoteIndex != -1) {
            for (Alarm note : alarmList) {
                if (note.alarmId == lastClickedUserNoteIndex) {
                    selectedNote = note;
                    break;
                }
            }
        }
        clock_window.putExtra("selectedNote", selectedNote);
        startActivityForResult(clock_window, 1);
    }

    private void addNoteToLayout(Alarm note){

        LinearLayout newNoteLayout = (LinearLayout) View.inflate(this, R.layout.user_note, null);
        newNoteLayout.setId((int) note.alarmId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        params.setMargins(0, 30, 0, 30);

        newNoteLayout.setLayoutParams(params);
        TextView time = (TextView) newNoteLayout.findViewWithTag("time_day_of_week")
                .findViewWithTag("time");
        time.setText(timeInMillisToTime(note.getTimeInMillis()));

        TextView day_of_week = (TextView) newNoteLayout.findViewWithTag("time_day_of_week")
                .findViewWithTag("day_of_week");
        day_of_week.setText(dayNames[note.getDayOfWeek()]);

        TextView userNote = (TextView) newNoteLayout.findViewWithTag("text");
        userNote.setText(note.getNote());

        userNoteLayout.addView(newNoteLayout);

        newNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickedUserNoteIndex == v.getId()) {
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), getColor(R.color.light_green));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastClickedUserNoteIndex = -1;
                } else {
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), getColor(R.color.light_green));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        changeStrokeColor(v, getColor(R.color.red));
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
                            Alarm removableNote = null;
                            for(int i = 0; i < alarmList.size(); i++){
                                Alarm currentNote = alarmList.get(i);
                                if(currentNote.getId() == viewId){
                                    removableNote = currentNote;
                                    alarmList.remove(i);
                                    break;
                                }
                            }
                            database.delete(removableNote);
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

        Log.d("New note layout ID: ", String.valueOf(newNoteLayout.getId()));
    }
    private void changeStrokeColor(View v, int color){
        Drawable background = (Drawable) v.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.mutate();
        gradientDrawable.setStroke(3, color);
    }
    public void onNoteClick(View view){

    }
    private void printCloseNotes(){
        long timeInMillis = chosenTime.getTimeInMillis();
        for(Alarm note : alarmList){
            long closeTimeBarrier = note.getTimeInMillis() - timeInMillis;
            if(closeTimeBarrier <= Alarm.DAY){
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
        boolean  success = false;
        Alarm returnedAlarm = (Alarm) data.getSerializableExtra("result");

        if (resultCode == 1){

            try{
                long lastDatabaseId = database.insert(returnedAlarm);
                returnedAlarm.setId(lastDatabaseId);

                alarmList.add(returnedAlarm);
                success = true;
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(resultCode == 2){
            try{
                for(int i = 0; i < alarmList.size(); i++){
                    if(alarmList.get(i).alarmId == returnedAlarm.alarmId){
                        alarmList.set(i, returnedAlarm);
                        success = true;
                        break;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if(success){
            clearNoteLayout();
            printCloseNotes();
        }
        else{
            try{
                changeStrokeColor(findViewById(lastClickedUserNoteIndex), getColor(R.color.light_green));
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

    // Debug methods, have to be removed before release
    private void debugWriteToDB(){
        Alarm alarm = new Alarm(Calendar.getInstance(), 0, "TEST0");
        database.insert(alarm);
    }
    private void debugReadFromDB(){
        alarmList = database.getAll();

        //Log.d("DB_TESTING", "READ_START");

        Log.d("DB_TESTING", "READ_END");
    }
    private void debugTestDB(){
        Alarm note = new Alarm(Calendar.getInstance(), 0, "TEST1");
        note.alarmId = 0;

        long return_key = database.insert(note);
        alarmList = database.getAll();

        for (Alarm element : alarmList){
            addNoteToLayout(element);
        }

        Log.d("DB_TESTING", "CUSTOM_TEST_END");
    }
}