package com.example.clock;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;
    private List<AlarmNote> alarmNoteList;
    private AlarmNoteDao database;
    private final String[] dayNames = {"NULL", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private LinearLayout userNoteLayout;
    private Context mContext;
    private int lastClickedUserNoteIndex = -1;
    Calendar chosen_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmNoteList = new ArrayList<AlarmNote>();

        userNoteLayout = findViewById(R.id.userNoteTopLayout);

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(600);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mContext = this;

        database = App.getInstance().getDatabase().alarmNoteDao();
        alarmNoteList = database.getAll();

        for (AlarmNote element : alarmNoteList){
            addNoteToLayout(element);
        }

        chosen_time = Calendar.getInstance();
    }

    public void onAddClock(View view) {
        Intent clock_window = new Intent(this, TimePickerFull.class);
        AlarmNote selectedNote = null;
        if(lastClickedUserNoteIndex != -1) {
            for (AlarmNote note : alarmNoteList) {
                if (note.alarmNoteId == lastClickedUserNoteIndex) {
                    selectedNote = note;
                    break;
                }
            }
        }
        clock_window.putExtra("selectedNote", selectedNote);
        startActivityForResult(clock_window, 1);
    }

    private void addNoteToLayout(AlarmNote note){

        LinearLayout newNoteLayout = (LinearLayout) View.inflate(this, R.layout.user_note, null);
        newNoteLayout.setId((int) note.alarmNoteId);

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

        userNoteLayout.addView(newNoteLayout);


        newNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastClickedUserNoteIndex == v.getId()){
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), getColor(R.color.light_green));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    lastClickedUserNoteIndex = -1;
                } else{
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), getColor(R.color.light_green));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        changeStrokeColor(v, getColor(R.color.red));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    lastClickedUserNoteIndex = v.getId();
                }
            }
        });
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
        long timeInMillis = chosen_time.getTimeInMillis();
        for(AlarmNote note : alarmNoteList){
            long closeTimeBarrier = note.getTimeInMillis() - timeInMillis;
            if(closeTimeBarrier <= AlarmNote.DAY){
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
        if (resultCode == 1){
            AlarmNote new_note = (AlarmNote) data.getSerializableExtra("result");
            try{
                database.insert(new_note);
                alarmNoteList.add(new_note);
                success = true;
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(resultCode == 2){
            AlarmNote new_note = (AlarmNote) data.getSerializableExtra("result");
            try{
                database.update(new_note);
                for(int i = 0; i < alarmNoteList.size(); i++){
                    if(alarmNoteList.get(i).alarmNoteId == new_note.alarmNoteId){
                        alarmNoteList.set(i, new_note);
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
        AlarmNote alarmNote = new AlarmNote(Calendar.getInstance(), 0, "TEST0");
        database.insert(alarmNote);
    }
    private void debugReadFromDB(){
        alarmNoteList = database.getAll();

        //Log.d("DB_TESTING", "READ_START");

        Log.d("DB_TESTING", "READ_END");
    }
    private void debugTestDB(){
        AlarmNote note = new AlarmNote(Calendar.getInstance(), 0, "TEST1");
        note.alarmNoteId = 0;

        long return_key = database.insert(note);
        alarmNoteList = database.getAll();

        for (AlarmNote element : alarmNoteList){
            addNoteToLayout(element);
        }

        Log.d("DB_TESTING", "CUSTOM_TEST_END");
    }
}