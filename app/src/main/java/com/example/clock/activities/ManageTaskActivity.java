package com.example.clock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.databinding.ActivityManageTaskBinding;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.ManageTaskViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.GregorianCalendar;
import java.util.Objects;

public class ManageTaskActivity extends AppCompatActivity {

    ManageTaskViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityManageTaskBinding mActivityBinding;
    TextInputEditText nameText;
    TextInputLayout mRepeatModesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_manage_task);

        String mode =  getIntent()
                .getStringExtra("mode");

        Task managingTask = (Task) getIntent().getSerializableExtra("ManagingTask");
        Category managingTaskCategory = (Category) getIntent()
                .getSerializableExtra("ManagingTaskCategory");

        Project managingProject = (Project) getIntent().getSerializableExtra("ManagingProject");

        if(managingTask == null && mode.equals("Task")){
            managingTask = new Task(GregorianCalendar.getInstance().getTimeInMillis(),
                    0, "",  "",
                    "", App.getSettings().getLastCategory().first);
        }
        else if(managingProject == null && mode.equals("Project")){
            managingProject = new Project();
        }

        if(mode.equals("Task")) {
            mFactory = new ViewModelFactoryBase(
                    getApplication(),
                    App.getDatabase(),
                    managingTask
            );
        }
        else if(mode.equals("Project")){
            mFactory = new ViewModelFactoryBase(
                    getApplication(),
                    App.getDatabase(),
                    managingProject
            );
        }
        mViewModel = new ViewModelProvider(this, mFactory).get(ManageTaskViewModel.class);

        mActivityBinding.setViewmodel(mViewModel);

        nameText = findViewById(R.id.manage_task_text_name);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nameTextString = Objects.requireNonNull(nameText.getText()).toString();
                int isCorrect = isNameCorrect(nameTextString);
                if(isCorrect == 1){
                    nameText.setError("Задача должна иметь имя");
                }
                else if(isCorrect == 2){
                    nameText.setError("Слишком длинное имя");
                }
                else{
                    nameText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        TextInputEditText descriptionText = findViewById(R.id.manage_task_edit_text);
        if(mViewModel.mManagingTaskRepository.isTaskMode()){
            AutoCompleteTextView repeatModesView = findViewById(R.id.manage_task_repeat_text_view);
            final String[] repeatModes = new String[] {"Once", "Every day", "Every week", "Every month"};
            ArrayAdapter<String> repeatModesAdapter = new ArrayAdapter<>(
                    ManageTaskActivity.this,
                    R.layout.dropdown_repeatmodes_item,
                    repeatModes
            );
            repeatModesView.setAdapter(repeatModesAdapter);
            repeatModesView.setText(repeatModes[0], false);
            repeatModesView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedMode = repeatModesAdapter.getItem(position);
                mViewModel.mManagingTaskRepository.setRepeatModeString(selectedMode);
            });
        }
    }

    private int isNameCorrect(String name){
        if(name == null || name.length() == 0){
            return 1;
        }
        else if(name.length() > 20){
            return 2;
        }
        else{
            return 0;
        }
    }


    public void onExit(View view){
        String nameTextString = Objects.requireNonNull(nameText.getText()).toString();
        int isCorrect = isNameCorrect(nameTextString);
        MaterialAlertDialogBuilder errorDialog = new MaterialAlertDialogBuilder(this)
                .setPositiveButton("Хорошо", null);

        if(isCorrect == 1){
            nameText.setError("Задача должна иметь имя");
            errorDialog.setMessage("Задача не может быть создана без имени.");
            errorDialog.show();
            return;
        }
        else if(isCorrect == 2){
            nameText.setError("Слишком длинное имя");
            errorDialog.setMessage("Длина имени задачи не может превышать 20 символов.");
            errorDialog.show();
            return;
        }

        mViewModel.saveChanges();
        if(mViewModel.mManagingTaskRepository.isTaskMode()) {
            setResult(20); // 20 Task created
        }
        else if(mViewModel.mManagingTaskRepository.isProjectMode()){
            setResult(30); // 30 Project created
        }
        finish();
    }
}