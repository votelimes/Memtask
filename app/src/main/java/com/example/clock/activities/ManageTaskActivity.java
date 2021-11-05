package com.example.clock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.databinding.ActivityManageTaskBinding;
import com.example.clock.model.Category;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.ManageTaskViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;

import java.util.GregorianCalendar;

public class ManageTaskActivity extends AppCompatActivity {

    ManageTaskViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityManageTaskBinding mActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_manage_task);

        //AutoCompleteTextView repeatModesView = findViewById(R.id.manage_task_repeat_text_view);
        final String[] repeatModes = new String[] {"Once", "Every day", "Every week", "Every month"};
        ArrayAdapter<String> repeatModesAdapter = new ArrayAdapter<>(
                ManageTaskActivity.this,
                R.layout.card_list_item,
                repeatModes
        );
        /*repeatModesView.setAdapter(repeatModesAdapter);
        repeatModesView.setText(repeatModes[0], false);
        repeatModesView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMode = repeatModesAdapter.getItem(position);
            mViewModel.mManagingTaskRepository.setRepeatModeString(selectedMode);
        });*/

        Task managingTask = (Task) getIntent().getSerializableExtra("ManagingTask");
        Category managingTaskCategory = (Category) getIntent()
                .getSerializableExtra("ManagingTaskCategory");

        if(managingTask == null){
            managingTask = new Task(GregorianCalendar.getInstance().getTimeInMillis(),
                    0, "",  "",
                    "", 0);
        }

        mFactory = new ViewModelFactoryBase(
                getApplication(),
                App.getDatabase(),
                managingTask
                );
        mViewModel = new ViewModelProvider(this, mFactory).get(ManageTaskViewModel.class);

        mActivityBinding.setViewmodel(mViewModel);
    }

    public void onExit(){
        mViewModel.saveChanges();
        setResult(RESULT_OK);
        finish();
    }
}