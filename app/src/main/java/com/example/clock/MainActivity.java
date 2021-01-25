package com.example.clock;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;
    private List<AlarmNote> alarmNoteList;
    private AlarmNoteDao database;
    private String[] dayNames = {"NULL", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private LinearLayout userNoteLayout;
    private Context mContext;
    private int lastClickedUserNoteIndex = -1;

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

        //debugTestDB();
    }

    public void onAddClock(View view) {
        Intent clock_window = new Intent(this, TimePickerFull.class);
        AlarmNote selectedNote = null;
        for(AlarmNote note : alarmNoteList){
            if(note.alarmNoteId == lastClickedUserNoteIndex){
                selectedNote = note;
                break;
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


        userNoteLayout.addView(newNoteLayout);


        newNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    private void printCloseNotes(long timeInMillis){
        clearNoteLayout();
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
        if (resultCode == 1){
            AlarmNote new_note = (AlarmNote) data.getSerializableExtra("result");
            try{
                database.insert(new_note);
                alarmNoteList.add(new_note);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        if(resultCode == 2){
            AlarmNote new_note = (AlarmNote) data.getSerializableExtra("result");
            try{
                database.update(new_note);
                for(int i = 0; i < alarmNoteList.size(); i++){
                    if(alarmNoteList.get(i).alarmNoteId == new_note.alarmNoteId){
                        alarmNoteList.set(i, new_note);
                        break;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Utility methods
    public int pxToDp(Context context, int px) {
        return  ((int) (px / context.getResources().getDisplayMetrics().density));
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