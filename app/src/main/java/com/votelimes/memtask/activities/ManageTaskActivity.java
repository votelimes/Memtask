package com.votelimes.memtask.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.core.util.Supplier;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.databinding.ActivityManageTaskBinding;
import com.votelimes.memtask.model.Category;
import com.votelimes.memtask.model.ProjectAndTheme;
import com.votelimes.memtask.model.TaskData;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.storageutils.Tuple2;
import com.votelimes.memtask.viewmodels.ManageTaskViewModel;
import com.votelimes.memtask.viewmodels.MemtaskViewModelBase;
import com.votelimes.memtask.viewmodels.ViewModelFactoryBase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.shawnlin.numberpicker.NumberPicker;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class ManageTaskActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    ManageTaskViewModel mViewModel;
    ViewModelFactoryBase mFactory;
    ActivityManageTaskBinding mActivityBinding;
    TextInputEditText nameText;
    TextInputEditText categoryText;
    ExpandableLayout expandableColorLayout;
    int mode;
    Context mContext;
    long millis;

    final ActivityResultLauncher<Intent> ringtonePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    Ringtone r = RingtoneManager.getRingtone(this, uri);
                    mViewModel.mManagingTaskRepository.setRingtonePath(uri.toString());
                }
            });

    final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    this.getContentResolver()
                            .takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try {
                        mViewModel.mManagingTaskRepository.setImage(uri.toString());
                    }
                    catch (Exception e){
                        Log.d("MANAGE_TASK_ERROR: ", "Unable to get URI image path");
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_manage_task);

        mode =  getIntent().getIntExtra(MemtaskViewModelBase.MTP_MODE, -1);
        mContext = this;
        String itemID = getIntent().getStringExtra(MemtaskViewModelBase.MTP_ID);

        String categoryID = getIntent().getStringExtra(MemtaskViewModelBase.MTP_CATEGORY_ID);
        if(categoryID == null){
            categoryID = "";
        }

        String parentID = getIntent().getStringExtra(MemtaskViewModelBase.MTP_PARENT);
        if(parentID == null){
            parentID = "";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        mFactory = new ViewModelFactoryBase(
                getApplication(),
                App.getDatabase(),
                App.getSilentDatabase(),
                mode, itemID, categoryID, parentID
        );

        mViewModel = new ViewModelProvider(this, mFactory).get(ManageTaskViewModel.class);

        nameText = findViewById(R.id.manage_task_text_name);
        categoryText = findViewById(R.id.manage_task_category_text);
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
        AutoCompleteTextView repeatModesView = findViewById(R.id.manage_task_repeat_text_view);
        final String[] repeatModes = getResources().getStringArray(R.array.repeat_modes);
        ArrayAdapter<String> repeatModesAdapter = new ArrayAdapter<>(
                ManageTaskActivity.this,
                R.layout.dropdown_repeatmodes_item,
                repeatModes
        );
        repeatModesView.setAdapter(repeatModesAdapter);
        repeatModesView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMode = repeatModesAdapter.getItem(position);
            mViewModel.mManagingTaskRepository.setRepeatModeString(selectedMode);
        });

        TextInputEditText rangeText = findViewById(R.id.manage_task_text_range);
        TextInputLayout rangeLayout = findViewById(R.id.manage_task_layout_range);
        rangeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MaterialDatePicker datepicker = MaterialDatePicker
                            .Builder
                            .dateRangePicker()
                            .setTitleText("Дата начала — дата окончания")
                            .setSelection(
                                    new Pair<>(
                                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                            MaterialDatePicker.todayInUtcMilliseconds()
                                    )
                            )
                            .build();

                    datepicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                        @Override
                        public void onPositiveButtonClick(Pair<Long, Long> selection) {

                            mViewModel.mManagingTaskRepository
                                    .setRangeMillis(selection.first, selection.second);
                            datepicker.dismiss();
                            view.clearFocus();
                        }
                    });
                    datepicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rangeLayout.setEndIconVisible(true);
                            rangeLayout.setEndIconActivated(true);
                        }
                    });
                    datepicker.show(getSupportFragmentManager(), datepicker.toString());
                    view.clearFocus();
                }
            }
        });
        rangeLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rangeText.setText("");
            }
        });
        rangeLayout.setEndIconDrawable(com.google.android.material.R.drawable.mtrl_ic_cancel);


        TextInputEditText notificationLayout = findViewById(R.id.manage_task_text_notify);
        notificationLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    if(!mViewModel.mManagingTaskRepository.isAnyDaySelected()){
                        if(mViewModel.mManagingTaskRepository.getRepeatMode() != 0) {
                            new MaterialAlertDialogBuilder(ManageTaskActivity.this)
                                    .setTitle("Внимание")
                                    .setMessage("Для текущего режима повторения необходимо выбрать дни")
                                    .setNeutralButton("Хорошо", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                            return;
                        }
                    }
                    int repeatMode = mViewModel.mManagingTaskRepository.getRepeatMode();
                    if (repeatMode == 0) {
                        // DatePicker
                        MaterialDatePicker datepicker = MaterialDatePicker
                                .Builder
                                .datePicker()
                                .setTitleText("Дата уведомления")
                                .setSelection(
                                        MaterialDatePicker.todayInUtcMilliseconds()
                                )
                                .build();
                        // TimePicker
                        MaterialTimePicker timePicker = new MaterialTimePicker
                                .Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setTitleText("Время уведомления")
                                .build();

                        timePicker.addOnPositiveButtonClickListener(dialog -> {
                            millis = millis + (1000L * 60 * 60 * timePicker.getHour());
                            millis = millis + (1000L * 60 * timePicker.getMinute());

                            mViewModel.mManagingTaskRepository.setTaskNotificationMillis(millis);
                            view.clearFocus();
                        });

                        datepicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                millis = selection;
                                timePicker.show(getSupportFragmentManager(), timePicker.toString());
                            }
                        });
                        datepicker.show(getSupportFragmentManager(), datepicker.toString());
                    }
                    else if(repeatMode == 1 || repeatMode == 2 || repeatMode == 3){
                        // TimePicker
                        MaterialTimePicker timePicker = new MaterialTimePicker
                                .Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setTitleText("Время уведомления")
                                .build();

                        timePicker.addOnPositiveButtonClickListener(dialog -> {
                            Instant instant = Instant.now().truncatedTo(ChronoUnit.DAYS);

                            long instTest = instant.toEpochMilli();

                            millis = instant.toEpochMilli() + (1000L * 60 * 60 * timePicker.getHour());
                            millis = millis + (1000L * 60 * timePicker.getMinute());

                            mViewModel.mManagingTaskRepository.setTaskNotificationMillis(millis);
                            view.clearFocus();
                        });
                        timePicker.show(getSupportFragmentManager(), timePicker.toString());
                    }
                    /*MaterialDatePicker datepicker = MaterialDatePicker
                            .Builder
                            .datePicker()
                            .setTitleText("Дата начала — дата окончания")
                            .setSelection(
                                    MaterialDatePicker.todayInUtcMilliseconds()
                            )
                            .build();

                    datepicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                        @Override public void onPositiveButtonClick(Long selection) {
                            mViewModel.mManagingTaskRepository.setTaskNotificationMillis(selection);
                        }
                    });
                    datepicker.show(getSupportFragmentManager(), datepicker.toString());*/
                    view.clearFocus();
                }
            }
        });

        expandableColorLayout = findViewById(R.id.manage_task_color_expandable_layout);
        TextInputEditText themeText = findViewById(R.id.manage_task_theme_text);
        themeText.setOnFocusChangeListener(this);

        TextInputEditText ringtoneField = findViewById(R.id.manage_task_media_text);
        ringtoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите рингтон для уведомления");
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);


                    ringtonePickerLauncher.launch(intent);
                    view.clearFocus();
                }
            }
        });

        TextInputEditText categoryField = findViewById(R.id.manage_task_category_text);
        categoryField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){

                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(ManageTaskActivity.this);
                    builderSingle.setTitle("Выберите категорию");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManageTaskActivity.this, android.R.layout.select_dialog_singlechoice);
                    mViewModel.intermediateThemeAndCategory.getValue().second.forEach(category -> {
                        arrayAdapter.add(category.getName());
                    });
                    builderSingle.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewModel.mManagingTaskRepository
                                    .setCategory(mViewModel
                                            .intermediateThemeAndCategory
                                            .getValue()
                                            .second.get(which));
                            dialog.dismiss();
                        }
                    });
                    builderSingle.show();






                    view.clearFocus();
                }
            }
        });
        mViewModel.intermediateThemeAndCategory.observe(this, intermediateThemeCategoryObs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.manage_task_toolbar);
        Drawable drawable = getDrawable(R.drawable.ic_round_arrow_back_24);
        drawable.setTint(getColor(R.color.secondary));
        toolbar.setNavigationIcon(drawable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(mode == MemtaskViewModelBase.TASK_CREATING){
            toolbar.setTitle("Создание задачи");

        }
        else if(mode == MemtaskViewModelBase.TASK_EDITING){
            toolbar.setTitle("Изменение задачи");
        }
        else if(mode == MemtaskViewModelBase.PROJECT_CREATING){
            toolbar.setTitle("Создание проекта");
        }
        else if(mode == MemtaskViewModelBase.PROJECT_EDITING){
            toolbar.setTitle("Изменение проекта");
        }

        MaterialButton button1 = (MaterialButton) findViewById(R.id.manage_task_first_color_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите основной цвет")
                        .setPreferenceName("Выберите основной цвет")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingTaskRepository
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

        MaterialButton button2 = (MaterialButton) findViewById(R.id.manage_task_second_color_button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите дополнительный цвет")
                        .setPreferenceName("Выберите дополнительный цвет")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingTaskRepository
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

        MaterialButton button3 = (MaterialButton) findViewById(R.id.manage_task_first_text_color_button);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите цвет основного текста")
                        .setPreferenceName("Выберите цвет основного текста")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingTaskRepository
                                                .setFirstTextColor(envelope.getColor());
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

        MaterialButton button4 = (MaterialButton) findViewById(R.id.manage_task_second_text_color_button);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите цвет основного текста")
                        .setPreferenceName("Выберите цвет основного текста")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingTaskRepository
                                                .setSecondTextColor(envelope.getColor());
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

        MaterialButton button5 = (MaterialButton) findViewById(R.id.manage_task_icon_color_button);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(mContext)
                        .setTitle("Выберите цвет иконок")
                        .setPreferenceName("Выберите цвет иконок")
                        .setPositiveButton("Выбрать",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        mViewModel.mManagingTaskRepository
                                                .setIconColor(envelope.getColor());
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

        TextInputEditText button6 = (TextInputEditText) findViewById(R.id.manage_task_text_priority);
        TextInputLayout button6Layout = (TextInputLayout) findViewById(R.id.manage_task_layout_priority);

        button6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ManageTaskActivity.this);
                    // Get the layout inflater
                    LayoutInflater inflater = (ManageTaskActivity.this).getLayoutInflater();

                    View view2 = inflater.inflate(R.layout.matrix_picker, null);
                    MaterialButton button = (MaterialButton) view2.findViewById(R.id.mat_max);

                    MaterialButton btn1 = view2.findViewById(R.id.mat_max);
                    MaterialButton btn2 = view2.findViewById(R.id.high);
                    MaterialButton btn3 = view2.findViewById(R.id.med);
                    MaterialButton btn4 = view2.findViewById(R.id.min);

                    Supplier<Integer> disableStrokes = () -> {
                        btn1.setStrokeColor(ColorStateList.valueOf(getColor(R.color.transparent)));
                        btn2.setStrokeColor(ColorStateList.valueOf(getColor(R.color.transparent)));
                        btn3.setStrokeColor(ColorStateList.valueOf(getColor(R.color.transparent)));
                        btn4.setStrokeColor(ColorStateList.valueOf(getColor(R.color.transparent)));
                        return 0;
                    };

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()){
                                case R.id.mat_max:
                                    disableStrokes.get();
                                    btn1.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                                    mViewModel.mManagingTaskRepository.setImportance(0);
                                    break;
                                case R.id.high:
                                    disableStrokes.get();
                                    btn2.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                                    mViewModel.mManagingTaskRepository.setImportance(1);
                                    break;
                                case R.id.med:
                                    disableStrokes.get();
                                    btn3.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                                    mViewModel.mManagingTaskRepository.setImportance(2);
                                    break;
                                case R.id.min:
                                    disableStrokes.get();
                                    btn4.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                                    mViewModel.mManagingTaskRepository.setImportance(3);
                                    break;
                            }
                        }
                    };

                    builder.setTitle("Определите важность");
                    builder.setCancelable(true);

                    btn1.setOnClickListener(listener);
                    btn2.setOnClickListener(listener);
                    btn3.setOnClickListener(listener);
                    btn4.setOnClickListener(listener);

                    switch (mViewModel.mManagingTaskRepository.getImportance()){
                        case 0:
                            disableStrokes.get();
                            btn1.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                            break;
                        case 1:
                            disableStrokes.get();
                            btn2.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                            break;
                        case 2:
                            disableStrokes.get();
                            btn3.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                            break;
                        case 3:
                            disableStrokes.get();
                            btn4.setStrokeColor(ColorStateList.valueOf(getColor(R.color.def_blue)));
                            break;
                        default:
                            disableStrokes.get();
                            break;
                    }

                    builder.setView(view2)
                            // Add action buttons
                            .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mViewModel.mManagingTaskRepository.setImportance(-1);
                                }
                            });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();
                    view.clearFocus();
                }
            }
        });
        button6Layout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button6.setText("");
            }
        });
        button6Layout.setEndIconDrawable(com.google.android.material.R.drawable.mtrl_ic_cancel);

        TextInputEditText button7 = (TextInputEditText) findViewById(R.id.manage_task_text_duration);
        TextInputLayout button7Layout = (TextInputLayout) findViewById(R.id.manage_task_layout_duration);

        button7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    int duration;
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ManageTaskActivity.this);
                    androidx.appcompat.app.AlertDialog dialog = null;
                    LayoutInflater inflater = (ManageTaskActivity.this).getLayoutInflater();
                    View view2 = inflater.inflate(R.layout.number_picker, null);

                    NumberPicker np = view2.findViewById(R.id.number_picker);
                    np.setValue(mViewModel.mManagingTaskRepository.getDurationValue());

                    builder.setTitle("Выберите продолжительность задачи");
                    builder.setView(view2);
                    builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.mManagingTaskRepository.setDuration(np.getValue());
                            dialogInterface.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    view.clearFocus();
                }
            }
        });
        button7Layout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button7.setText("");
                mViewModel.mManagingTaskRepository.setDuration(0);
            }
        });
        button7Layout.setEndIconDrawable(com.google.android.material.R.drawable.mtrl_ic_cancel);

        TextInputEditText button8 = (TextInputEditText) findViewById(R.id.manage_task_text_image);
        TextInputLayout button8Layout = (TextInputLayout) findViewById(R.id.manage_task_layout_image);

        button8.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);
                    view.clearFocus();
                }
            }
        });
        button8Layout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button8.setText("");
                mViewModel.mManagingTaskRepository.setImage("");
            }
        });
        button8Layout.setEndIconDrawable(com.google.android.material.R.drawable.mtrl_ic_cancel);
    }

    private int isNameCorrect(String name){
        if(name == null || name.length() == 0 && false){
            return 1;
        }
        else if(name.length() > 180){
            return 2;
        }
        else{
            return 0;
        }
    }

    public void onExit(View view){
        String nameTextString = Objects.requireNonNull(nameText.getText()).toString();
        String categoryTextString = Objects.requireNonNull(categoryText.getText()).toString();
        int isCorrect = isNameCorrect(nameTextString);
        MaterialAlertDialogBuilder errorDialog = new MaterialAlertDialogBuilder(this)
                .setPositiveButton("Хорошо", null);

        if(categoryTextString.equals("")){
            MaterialAlertDialogBuilder errorDialog2 = new MaterialAlertDialogBuilder(this)
                    .setMessage("Необходимо выбрать категорию")
                    .setPositiveButton("Хорошо", null);
            errorDialog2.show();
            return;
        }

        mViewModel.saveChanges(this);
        if(mode == MemtaskViewModelBase.TASK_CREATING){
            setResult(20); // 20 Task created
        }
        else if(mode == MemtaskViewModelBase.TASK_EDITING){
            setResult(21); // 30
        }
        else if(mode == MemtaskViewModelBase.PROJECT_CREATING){
            setResult(30); // 30 Project created
        }
        else if(mode == MemtaskViewModelBase.PROJECT_EDITING){
            setResult(31); // 30 Project created
        }
        finish();
    };

    public boolean isCategoryCorrect(int categoryID){
        if(categoryID == -1){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            if(expandableColorLayout.isExpanded()){
                expandableColorLayout.collapse();
            }
            else{
                expandableColorLayout.expand();
            }
            view.clearFocus();
        }
    }

    final Observer<Tuple2<List<Theme>, List<Category>>> intermediateThemeCategoryObs = new Observer<Tuple2<List<Theme>, List<Category>>>() {
        @Override
        public void onChanged(Tuple2<List<Theme>, List<Category>> data) {
            if(mode == MemtaskViewModelBase.TASK_CREATING || mode == MemtaskViewModelBase.PROJECT_CREATING){
                mViewModel.initCreating();
                mActivityBinding.setViewmodel(mViewModel);
            }
            else if(mode == MemtaskViewModelBase.TASK_EDITING){
                mViewModel.taskLiveData.observe(ManageTaskActivity.this, taskObs);
            }
            else if(mode == MemtaskViewModelBase.PROJECT_EDITING){
                mViewModel.projectLiveData.observe(ManageTaskActivity.this, projectObs);
            }
        }
    };


    final Observer<TaskData> taskObs = new Observer<TaskData>() {
        @Override
        public void onChanged(TaskData data) {
            if(mode == MemtaskViewModelBase.TASK_EDITING){
                mViewModel.initTaskEditing();
            }
            else{
                mViewModel.initCreating();
            }

            mActivityBinding.setViewmodel(mViewModel);
        }
    };
    final Observer<ProjectAndTheme> projectObs = new Observer<ProjectAndTheme>() {
        @Override
        public void onChanged(ProjectAndTheme data) {
            if(mode == MemtaskViewModelBase.PROJECT_EDITING){
                mViewModel.initProjectEditing();
            }
            else{
                mViewModel.initCreating();
            }
            mActivityBinding.setViewmodel(mViewModel);
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        if(mode == MemtaskViewModelBase.TASK_CREATING){
            setResult(20); // 20 Task created
        }
        else if(mode == MemtaskViewModelBase.TASK_EDITING){
            setResult(21); // 30
        }
        else if(mode == MemtaskViewModelBase.PROJECT_CREATING){
            setResult(30); // 30 Project created
        }
        else if(mode == MemtaskViewModelBase.PROJECT_EDITING){
            setResult(31); // 30 Project created
        }
        onBackPressed();
        return true;
    }
}