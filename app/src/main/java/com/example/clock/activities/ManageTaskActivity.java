package com.example.clock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.clock.R;
import com.example.clock.databinding.ActivityManageTaskBinding;
import com.example.clock.viewmodels.ManageTaskViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;

public class ManageTaskActivity extends AppCompatActivity {

    ManageTaskViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityManageTaskBinding mActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_manage_task);

        AutoCompleteTextView repeatModesView = findViewById(R.id.manage_task_repeat_text_view);
        final String[] repeatModes = new String[] {"Once", "Every day", "Every week", "Every month"};
        ArrayAdapter<String> repeatModesAdapter = new ArrayAdapter<>(
                ManageTaskActivity.this,
                R.layout.card_list_item,
                repeatModes
        );
        repeatModesView.setAdapter(repeatModesAdapter);
        repeatModesView.setText(repeatModes[0], false);
        repeatModesView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMode = repeatModesAdapter.getItem(position);
            mViewModel.mManagingTaskRepository.setRepeatModeString(selectedMode);
        });

        mFactory = new ViewModelFactoryBase(
                getApplication(),
                getIntent().getSerializableExtra("ManagingTask")
                );
        mViewModel = new ViewModelProvider(this, mFactory).get(ManageTaskViewModel.class);

        mActivityBinding.setViewmodel(mViewModel);
    }

    public void onClick(View view){
        //mViewModel.mManagingTaskRepository.setTaskDescription("New Descr");
        //mViewModel.mManagingTaskRepository.setRepeatModeString("Every day");
    }
}