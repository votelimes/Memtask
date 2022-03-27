package com.example.clock.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.databinding.ActivityManageCategoryBinding;
import com.example.clock.model.Category;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.ManageCategoryViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.Random;

public class ManageCategoryActivity extends AppCompatActivity {

    ManageCategoryViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityManageCategoryBinding mActivityBinding;
    TextInputLayout nameLayout;
    TextInputEditText nameText;
    MaterialButton firstColorButton;
    MaterialButton secondColorButton;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        mContext = this;

        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_manage_category);

        Category managingCategory = (Category) getIntent().getSerializableExtra("ManagingCategory");

        if(managingCategory == null){
            managingCategory = new Category("", "", "");
        }

        mFactory = new ViewModelFactoryBase(
                getApplication(),
                App.getDatabase(),
                App.getSilentDatabase(),
                managingCategory
        );
        mViewModel = new ViewModelProvider(this, mFactory).get(ManageCategoryViewModel.class);

        mViewModel.getThemesLiveData(getApplication(), App.getDatabase(), App.getSilentDatabase())
                .observe(this, data -> {
            if(data == null){
                return;
            }
            final int themesArraySize = data.size();
            if(themesArraySize == 0){
                return;
            }

            final Random random = new Random();
            int selectedThemeIndex = random
                    .ints(0, themesArraySize)
                    .findFirst()
                    .getAsInt();

            Theme randomTheme = data.get(selectedThemeIndex);
            if(mViewModel.mManagingCategoryRepository.getThemeID() == "") {
                mViewModel.mManagingCategoryRepository.setThemeID(randomTheme.getID());
            }
        });


        nameLayout = findViewById(R.id.manage_category_name_layout);
        nameText = findViewById(R.id.manage_category_text_name);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nameTextString = nameText.getText().toString();
                int isCorrect = isNameCorrect(nameTextString);
                if(isCorrect == 1){
                    nameText.setError("Категория должна иметь имя");
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

        firstColorButton = findViewById(R.id.manage_category_first_color_button);
        secondColorButton = findViewById(R.id.manage_category_second_color_button);

        firstColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите основной цвет")
                        .setPreferenceName("Выберите основной цвет")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingCategoryRepository
                                                .setFirstColor(envelope.getColor());
                                    }
                                })
                        .setNegativeButton("Отменить",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(false) // the default value is true.
                        .attachBrightnessSlideBar(true)  // the default value is true.
                        .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                        .show();
            }
        });
        secondColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите дополнительный цвет")
                        .setPreferenceName("Выберите дополнительный цвет")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingCategoryRepository
                                                .setSecondColor(envelope.getColor());
                                    }
                                })
                        .setNegativeButton("Отменить",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(false) // the default value is true.
                        .attachBrightnessSlideBar(true)  // the default value is true.
                        .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                        .show();
            }
        });

        mActivityBinding.setViewmodel(mViewModel);
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
        String nameTextString = nameText.getText().toString();
        int isCorrect = isNameCorrect(nameTextString);
        MaterialAlertDialogBuilder errorDialog = new MaterialAlertDialogBuilder(this)
                .setPositiveButton("Хорошо", null);

        if(isCorrect == 1){
            nameText.setError("Категория должна иметь имя");
            errorDialog.setMessage("Категория не может быть создана без имени.");
            errorDialog.show();
            return;
        }
        else if(isCorrect == 2){
            nameText.setError("Слишком длинное имя");
            errorDialog.setMessage("Длина имени категории не может превышать 20 символов.");
            errorDialog.show();
            return;
        }

        mViewModel.saveChanges();
        setResult(10); // 10 Save category result
        finish();
    }
}