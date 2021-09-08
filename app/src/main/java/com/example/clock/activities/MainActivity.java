package com.example.clock.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.adapters.MainActivityListAdapter;
import com.example.clock.app.App;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.example.clock.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // REMOVING, OLD
    // Dynamic variables
    private Context mContext;
    private int lastClickedUserNoteIndex = -1;
    private LiveData<List<Task>> alarmsData;
    private boolean ignoreUpdate = false;
    private final int taskFieldId = 10020;
    private final int ideaFieldId = 10021;
    private final int noteFieldId = 10022;
    private int lastProjectFieldId = 10022;

    // Types
    private final String[] dayNames = {"NULL", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private enum FieldType {TASK, IDEA, PROJECT}
    private enum LayoutType {LINEAR, CONSTRAINT}

    // NEW, NOT REMOVING
    MainViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityMainBinding mActivityBinding;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        //Unpack result
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        mFactory = new ViewModelFactoryBase(getApplication());
        mViewModel = new ViewModelProvider(this, mFactory).get(MainViewModel.class);
        mActivityBinding.setViewmodel(mViewModel);

        MainActivityListAdapter mainTaskListAdapter =
                new MainActivityListAdapter(this);

        mContext = this;


        //addViewToLayout(R.id.fieldTopLayout, R.id.mainStrutMiddle,  FieldType.TASK);

        alarmsData = App.getInstance().getAlarmsLiveData();
        alarmsData.observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(!isUpdateDisabled()) {
                    //clearLayout(taskFieldId, LayoutType.LINEAR);
                    //printCloseNotes(tasks);
                }
                else{
                    enableUpdate();
                }
            }
        });

        /*// Clock text view init
        final Handler handler = new Handler();
        Timer    timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            updateMainClock();
                        }
                        catch (Exception e) {
                            Log.d("ERROR:", "Unable to create timer event #1 main");
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60);*/
    }

    public void onAddClock(View view) {

        Intent intent = new Intent(this, ManageTaskActivity.class);

        if(lastClickedUserNoteIndex == -1){
            intent.putExtra("ManagingTask", new Task(Calendar
                    .getInstance(), 0, ""));
        }
        else{
            intent.putExtra("ManagingTask", new Task(Calendar
                    .getInstance(), 0, ""));
        }

        activityLauncher.launch(intent);


//        Intent clock_window = new Intent(this, CreateAlarmActivity.class);
//        Task selectedNote = null;
//        if(lastClickedUserNoteIndex != -1) {
//            for (Task note : App.getInstance().getAlarmsLiveData().getValue()) {
//                if (note.getTaskId() == lastClickedUserNoteIndex) {
//                    selectedNote = note;
//                    break;
//                }
//            }
//        }
//        clock_window.putExtra("selectedNote", selectedNote);
//        startActivityForResult(clock_window, 1);
    }

    public void onNoteClick(View view){
        addViewToLayout(R.id.fieldTopLayout, lastProjectFieldId,  FieldType.TASK);
    }

    private void addTaskToLayout(Task task){
        LinearLayout userNoteLayout = (LinearLayout) findViewById(taskFieldId);

        LinearLayout newNoteLayout = (LinearLayout) View.inflate(this, R.layout.user_note, null);
        newNoteLayout.setId((int) task.getTaskId());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        params.setMargins(0, 30, 0, 30);

        newNoteLayout.setLayoutParams(params);
        TextView time = (TextView) newNoteLayout.findViewWithTag("time_day_of_week")
                .findViewWithTag("time");
        time.setText(timeInMillisToTime(task.getTimeInMillis()));

        TextView day_of_week = (TextView) newNoteLayout.findViewWithTag("time_day_of_week")
                .findViewWithTag("day_of_week");
        day_of_week.setText(dayNames[task.getDayOfWeek()]);

        TextView userNote = (TextView) newNoteLayout.findViewWithTag("text");
        userNote.setText(task.getDescription());

        SwitchCompat enableSwitch = newNoteLayout.
                findViewWithTag("switch_layout").findViewWithTag("switch");

        userNoteLayout.addView(newNoteLayout);

        changeStrokeColor(newNoteLayout, App.Settings.getColor("mainTheme5"));

        newNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickedUserNoteIndex == v.getId()) {
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), App.Settings.getColor("mainTheme5"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastClickedUserNoteIndex = -1;
                } else {
                    try {
                        changeStrokeColor(findViewById(lastClickedUserNoteIndex), App.Settings.getColor("mainTheme5"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        changeStrokeColor(v, App.Settings.getColor("mainTheme4"));
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
                LinearLayout currentTaskLayout = (LinearLayout) activeSwitch.getParent().getParent();
                long currentTaskId = currentTaskLayout.getId();

                Task currentTask = App.getInstance().getById(currentTaskId);
                boolean previousState = currentTask.isEnabled();
                currentTask.setEnabled(activeSwitch.isChecked());

                long currentTimeInMillis = System.currentTimeMillis();
                if(currentTask.getTimeInMillis() > currentTimeInMillis + 3000) {
                    if (previousState == false) {
                        currentTask.schedule(getApplicationContext());
                        currentTask.setEnabled(true);
                    }
                    if (previousState == true){
                        currentTask.cancelAlarm(getApplicationContext());
                        currentTask.setEnabled(false);
                    }
                }
                App.getInstance().update(currentTask);
            }
        });

        enableSwitch.setChecked(task.isEnabled());
    }
    private void addViewToLayout(int constraintLParentId, int topOfFieldId, FieldType type){

        int bgColor = App.Settings.getColor("mainTheme3");

        ConstraintLayout parentView = (ConstraintLayout) findViewById(constraintLParentId);
        LinearLayout newLayout = new LinearLayout(mContext);
        newLayout.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradientDrawable   =   new GradientDrawable();
        gradientDrawable.setCornerRadii(new float[]{20, 20, 20, 20, 20, 20, 20, 20});
        gradientDrawable.setColor(bgColor);
        newLayout.setBackground(gradientDrawable);

        if(type == FieldType.TASK) {
            newLayout.setId(taskFieldId);

            newLayout.setPadding(15, 15, 15, 15);
            parentView.addView(newLayout);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(parentView);

            constraintSet.connect(newLayout.getId(), ConstraintSet.START, R.id.parent, ConstraintSet.START, 0);

            if (topOfFieldId == 10022) {
                constraintSet.connect(newLayout.getId(), ConstraintSet.TOP, R.id.mainStrutMiddle, ConstraintSet.BOTTOM, 0);
            } else {
                constraintSet.connect(newLayout.getId(), ConstraintSet.TOP, topOfFieldId, ConstraintSet.BOTTOM, 0);
            }
            constraintSet.applyTo(parentView);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newLayout.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;

            params.topMargin = 100;
            params.leftMargin = 20;
            params.rightMargin = 20;

            newLayout.setLayoutParams(params);
        }
        else if(type == FieldType.IDEA){
            newLayout.setId(ideaFieldId);
        }
        else if(type == FieldType.PROJECT){
            newLayout.setId(lastProjectFieldId + 1);
            lastProjectFieldId++;


        }
    }
    private void changeStrokeColor(View v, int color){
        Drawable background = (Drawable) v.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.mutate();
        gradientDrawable.setStroke(3, color);
    }
    private void printCloseNotes(List<Task> tasks){
        long timeInMillis = System.currentTimeMillis();

        for(Task note : tasks){
            long closeTimeBarrier = Math.abs(note.getTimeInMillis() - timeInMillis);
            // Debug. Remove true statement.
            if(closeTimeBarrier <= Task.DAY || true){
                addTaskToLayout(note);
            }
        }
    }
    private void clearLayout(int layoutId, LayoutType type){

        if(type == LayoutType.LINEAR){
            LinearLayout layout = (LinearLayout) findViewById(layoutId);
            layout.removeAllViews();
        }
        else if(type == LayoutType.CONSTRAINT){
            ConstraintLayout layout = (ConstraintLayout) findViewById(layoutId);
            layout.removeAllViews();
        }
    }
    private void updateMainClock(){
        TextView timeView = (TextView) findViewById(R.id.mainClockTextView);
        TextView timeViewIndex = (TextView) findViewById(R.id.mainClockTimeIndex);
        if(App.Settings.is24HTimeUses) {
            SimpleDateFormat hoursFormatter = new SimpleDateFormat("HH:mm");
            timeView.setText(hoursFormatter.format(new Date()));
            timeViewIndex.setText("");
        }
        else{
            SimpleDateFormat hoursFormatter = new SimpleDateFormat("hh:mm aa");
            String timeString = hoursFormatter.format(new Date());
            String[] time = timeString.split(" ");

            timeView.setText(time[0]);
            timeViewIndex.setText(time[1]);
        }
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 1 || resultCode != 2){
            try{
                changeStrokeColor(findViewById(lastClickedUserNoteIndex), App.Settings.getColor("mainTheme4"));
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