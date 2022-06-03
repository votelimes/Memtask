package com.example.clock.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.activities.MapActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CalendarTaskBinding;
import com.example.clock.model.TaskData;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CalendarViewModel;
import com.example.clock.viewmodels.MemtaskViewModelBase;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.threeten.bp.LocalDate;

import java.util.Date;
import java.util.List;

public class CalendarFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_UNDEFINED = 0;
    private static final int VIEW_TYPE_TASK = 1;
    private static final int VIEW_TYPE_CALENDAR = 2;

    private final ActivityResultLauncher<Intent> resultLauncher;
    private final CalendarViewModel mViewModel;
    private MaterialCalendarView calendar;
    private LifecycleOwner lifecycleOwner;
    private CalendarDay currentDate;
    private TextView noTasksInformer;
    private Snackbar mItemHasBeenDeletedSnack;

    private RecyclerView.SmoothScroller smoothScroller;
    private final RecyclerView.LayoutManager mLayoutManager;

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnLayoutChangeListener{
        private final CalendarTaskBinding binding;
        private final MaterialCardView mainLayout;
        private final ConstraintLayout mainConstraint;

        private final TextView range;
        private final ImageView important;

        private final LinearLayout categoryLayout;
        private final Drawable categoryLayoutDrawable;
        private final TextView categoryName;
        private final EditText name;
        private final EditText description;
        private final ImageView alarmImage;
        private final TextView alarmTime;

        private Uri imageUri = null;
        private Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mainConstraint.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        public TaskViewHolder(CalendarTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View view = binding.getRoot();
            mainLayout = (MaterialCardView) view.findViewById(R.id.task_top_layout);
            mainConstraint = (ConstraintLayout) view.findViewById(R.id.main_constraint);
            range = (TextView) view.findViewById(R.id.task_range);
            important = (ImageView) view.findViewById(R.id.task_important);
            categoryLayout = (LinearLayout) view.findViewById(R.id.task_category_layout);
            categoryLayoutDrawable = categoryLayout.getBackground();
            categoryName = (TextView) view.findViewById(R.id.task_category_name);
            name = (EditText) view.findViewById(R.id.task_name);
            description = (EditText) view.findViewById(R.id.task_description);
            alarmImage = (ImageView) view.findViewById(R.id.task_alarm_image);
            alarmTime = (TextView) view.findViewById(R.id.task_alarm_time);
        }

        public void bind(CalendarViewModel vm, CalendarViewModel.TaskObserver data){
            binding.setVm(vm);
            binding.setData(data);
            binding.executePendingBindings();
        }

        public CalendarTaskBinding getBinding() {
            return binding;
        }

        public MaterialCardView getMainLayout() {
            return mainLayout;
        }

        public TextView getRange() {
            return range;
        }

        public ImageView getImportant() {
            return important;
        }

        public LinearLayout getCategoryLayout() {
            return categoryLayout;
        }

        public TextView getCategoryName() {
            return categoryName;
        }

        public EditText getName() {
            return name;
        }

        public EditText getDescription() {
            return description;
        }

        public ImageView getAlarmImage() {
            return alarmImage;
        }

        public TextView getAlarmTime() {
            return alarmTime;
        }

        public void setImageUri(Uri imageUri) {
            this.imageUri = imageUri;
        }

        @Override
        public void onLayoutChange(View view,
                                   int left, int top, int right, int bottom,
                                   int leftWas, int topWas, int rightWas, int bottomWas) {
            Picasso.get()
                    .load(imageUri)
                    .into(target);
        }

        public void bindLayoutChange(){
            mainLayout.addOnLayoutChangeListener(this);
        }

        public ConstraintLayout getMainConstraint() {
            return mainConstraint;
        }
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder implements OnDateSelectedListener, OnRangeSelectedListener, OnMonthChangedListener {
        MaterialCalendarView calendar;
        MinLoadDayDecorator minLoadDecorator;
        MedLoadDayDecorator medLoadDecorator;
        HighLoadDayDecorator highLoadDecorator;
        MaxLoadDayDecorator maxLoadDecorator;
        TodayDecorator todayDecorator;

        public CalendarViewHolder(View view) {
            super(view);
            calendar = view.findViewById(R.id.calendar);

            noTasksInformer = view.findViewById(R.id.no_tasks_message);
            minLoadDecorator = new MinLoadDayDecorator();
            medLoadDecorator = new MedLoadDayDecorator();
            highLoadDecorator = new HighLoadDayDecorator();
            maxLoadDecorator = new MaxLoadDayDecorator();
            todayDecorator = new TodayDecorator(view.getContext());

            calendar.setTitleMonths(R.array.calendar_month_names);
            if(currentDate == null){
                currentDate = CalendarDay.today();
            }
            else if(CalendarDay.today().getMonth() != currentDate.getMonth()) {
                calendar.setSelectedDate(currentDate);
            }

            calendar.setCurrentDate(currentDate);
            calendar.addDecorators(todayDecorator, minLoadDecorator, medLoadDecorator,
                    highLoadDecorator, maxLoadDecorator);
            calendar.setOnDateChangedListener(this);
            calendar.setOnRangeSelectedListener(this);
            calendar.setOnMonthChangedListener(this);
            mViewModel.updateMonthTasksPack().observe(lifecycleOwner, monthChangeObserver);
        }
        public MaterialCalendarView getCalendar(){
            return calendar;
        }
        public TextView getNoTasksInformer() {
            return noTasksInformer;
        }

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            if(selected){
                currentDate = date;
                mViewModel.setDateAndUpdate(date, null);
            }
            getNoTasksInformer().setVisibility(mViewModel.getPoolSize() == 0 ? View.VISIBLE : View.GONE);
            notifyDataSetChanged();
        }

        @Override
        public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
            CalendarDay firstSelected = dates.get(0);
            CalendarDay lastSelected = dates.get(dates.size() - 1);
            if(firstSelected.getDate().toEpochDay() > lastSelected.getDate().toEpochDay()){
                mViewModel.setDateAndUpdate(lastSelected, firstSelected);
            }
            else if(firstSelected.getDate().toEpochDay() < lastSelected.getDate().toEpochDay()){
                mViewModel.setDateAndUpdate(firstSelected, lastSelected);
            }
            else{
                mViewModel.setDateAndUpdate(firstSelected, null);
            }
            getNoTasksInformer().setVisibility(mViewModel.getPoolSize() == 0 ? View.VISIBLE : View.GONE);
            notifyDataSetChanged();
        }

        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            currentDate = date;
            if(CalendarDay.today().getMonth() != currentDate.getMonth()) {
                widget.setSelectedDate(date);
            }
            else{
                widget.setSelectedDate(CalendarDay.today());
            }
            widget.invalidateDecorators();
            mViewModel.setDate(date, null);
            mViewModel.requestMonthTasksPack().removeObservers(lifecycleOwner);
            mViewModel.updateMonthTasksPack().observe(lifecycleOwner, monthChangeObserver);
        }

        public class MinLoadDayDecorator implements DayViewDecorator {

            private CalendarDay today;
            private final int[] intervals = App.getInstance()
                    .getResources().getIntArray(R.array.calendar_day_load_interval);

            public MinLoadDayDecorator() {
                today = CalendarDay.today();
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                int currentDayLoad = mViewModel.getDayLoad(day.getDay() - 1);
                return currentDayLoad > intervals[0] && currentDayLoad <= intervals[1];
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new StyleSpan(Typeface.BOLD));
                view.addSpan(new RelativeSizeSpan(1.4f));
                view.addSpan(new DotSpan(10, App.getInstance().getColor(R.color.minDayLoad)));
            }

            public void setDate(LocalDate date) {
                this.today = CalendarDay.from(date);
            }
        }
        public class MedLoadDayDecorator implements DayViewDecorator {

            private CalendarDay today;
            private final int[] intervals = App.getInstance()
                    .getResources().getIntArray(R.array.calendar_day_load_interval);

            public MedLoadDayDecorator() {
                today = CalendarDay.today();
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                int currentDayLoad = mViewModel.getDayLoad(day.getDay() - 1);
                return currentDayLoad > intervals[1] && currentDayLoad <= intervals[2];
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new StyleSpan(Typeface.BOLD));
                view.addSpan(new RelativeSizeSpan(1.4f));
                view.addSpan(new DotSpan(10, App.getInstance().getColor(R.color.mediumDayLoad)));
            }

            public void setDate(LocalDate date) {
                this.today = CalendarDay.from(date);
            }
        }
        public class HighLoadDayDecorator implements DayViewDecorator {
            private CalendarDay today;
            private final int[] intervals = App.getInstance()
                    .getResources().getIntArray(R.array.calendar_day_load_interval);

            public HighLoadDayDecorator() {
                today = CalendarDay.today();
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                int currentDayLoad = mViewModel.getDayLoad(day.getDay() - 1);
                return currentDayLoad > intervals[2] && currentDayLoad <= intervals[3];
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new StyleSpan(Typeface.BOLD));
                view.addSpan(new RelativeSizeSpan(1.4f));
                view.addSpan(new DotSpan(10, App.getInstance().getColor(R.color.highDayLoad)));
            }

            public void setDate(LocalDate date) {
                this.today = CalendarDay.from(date);
            }
        }
        public class MaxLoadDayDecorator implements DayViewDecorator {

            private CalendarDay today;
            private final int[] intervals = App.getInstance()
                    .getResources().getIntArray(R.array.calendar_day_load_interval);

            public MaxLoadDayDecorator() {
                today = CalendarDay.today();
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                int currentDayLoad = mViewModel.getDayLoad(day.getDay() - 1);
                return currentDayLoad > intervals[3];
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new StyleSpan(Typeface.BOLD));
                view.addSpan(new RelativeSizeSpan(1.4f));
                view.addSpan(new DotSpan(10, App.getInstance().getColor(R.color.maxDayLoad)));
            }

            public void setDate(LocalDate date) {
                this.today = CalendarDay.from(date);
            }
        }
        public class TodayDecorator implements DayViewDecorator {
            private final Drawable drawable;

            public TodayDecorator(Context context) {
                drawable = context.getDrawable(R.drawable.today_calendar_decorator);
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {

                return day.getDate().toEpochDay() == LocalDate.now().toEpochDay();
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setSelectionDrawable(drawable);
            }
        }
    }

    public CalendarFragmentAdapter(ActivityResultLauncher<Intent> resultLauncher,
                                   CalendarViewModel viewModel, LifecycleOwner lcowner, View rootView, RecyclerView.LayoutManager layoutManager) {
        mViewModel = viewModel;
        this.resultLauncher = resultLauncher;
        this.lifecycleOwner = lcowner;

        mLayoutManager = layoutManager;
        smoothScroller = new LinearSmoothScroller(rootView.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        mItemHasBeenDeletedSnack = Snackbar.make(rootView, "", (int) CalendarViewModel.RESTORE_ITEM_SNACKBAR_TIME);

        View snackBarView = mItemHasBeenDeletedSnack.getView();
        snackBarView.setTranslationY(-(convertDpToPixel(75, snackBarView.getContext())));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        if (viewType == VIEW_TYPE_TASK) {
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewGroup.getContext());
            CalendarTaskBinding calBinding =
                    CalendarTaskBinding.inflate(layoutInflater, viewGroup, false);

            return new TaskViewHolder(calBinding);
        }
        else if(viewType == VIEW_TYPE_CALENDAR){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.calendar, viewGroup, false);
            return new CalendarViewHolder(view);
        }

        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder currentViewHolder, final int position) {
        if(getItemViewType(position) == VIEW_TYPE_TASK) {
            CalendarViewModel.TaskObserver taskObs = mViewModel.getTaskObserver(position - 1);

            TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

            viewHolder.bind(mViewModel, taskObs);
            viewHolder.getMainConstraint().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Выберите действие")
                            .setItems(R.array.task_dialog_short, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0: // Изменить
                                            Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                                            intent.putExtra(MemtaskViewModelBase.MTP_ID, taskObs.getTask().getTaskId());
                                            intent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.TASK_EDITING);

                                            resultLauncher.launch(intent);
                                            break;
                                        case 1: // Контакты

                                            break;
                                        case 2: // Адреса
                                            Intent intentMap = new Intent(view.getContext(), MapActivity.class);
                                            intentMap.putExtra(MemtaskViewModelBase.MTP_ID, taskObs.getTask().getTaskId());
                                            resultLauncher.launch(intentMap);
                                            break;
                                        case 3: // Удалить
                                            removeItem(currentViewHolder.getAbsoluteAdapterPosition());
                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });
            viewHolder.getMainConstraint().setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    viewHolder.getMainLayout().toggle();
                    viewHolder.getBinding().getData().setCompleted(viewHolder.getMainLayout().isChecked());
                    viewHolder.getBinding().getData().setNotificationEnabled(view.getContext(), !viewHolder.getMainLayout().isChecked());
                }

                @Override
                public void onDoubleClick(View view) {
                    if(!taskObs.getCompletedOrExpired()){
                        int code = taskObs.setNotificationEnabled(view.getContext(),
                                !taskObs.getNotificationEnabled());
                        if(code == 1){
                            Toast.makeText(view.getContext(), "Не выбрано время уведомления", Toast.LENGTH_SHORT).show();
                        }
                        else if(code == 2){
                            Toast.makeText(view.getContext(), "Время уведомления уже прошло", Toast.LENGTH_SHORT).show();
                        }
                        else if(code == 0){
                            Toast.makeText(view.getContext(), "Уведомление установлено", Toast.LENGTH_SHORT).show();
                        }
                        else if(code == -1){
                            Toast.makeText(view.getContext(), "Уведомление выключено", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }));

            //Colors binding
            Theme theme = taskObs.getTheme();
            if(theme != null) {
                viewHolder.getMainLayout().setCardBackgroundColor(theme.getFirstColor());
                viewHolder.getCategoryLayout().getBackground().setTint(theme.getSecondColor());

                viewHolder.getName().setTextColor(theme.getMainTextColor());
                viewHolder.getName().setHintTextColor(theme.getIconColor());
                viewHolder.getDescription().setTextColor(theme.getMainTextColor());

                viewHolder.getRange().setTextColor(theme.getAdditionalTextColor());
                viewHolder.getAlarmTime().setTextColor(theme.getAdditionalTextColor());

                viewHolder.getImportant().setColorFilter(theme.getIconColor());
                viewHolder.getAlarmImage().setColorFilter(theme.getIconColor());

                String uriString = viewHolder.getBinding().getData().getImage();
                if(uriString != null && uriString.length() != 0){
                    if(uriString != null && uriString.length() != 0){
                        Uri uri = Uri.parse(uriString);
                        viewHolder.setImageUri(uri);
                        viewHolder.bindLayoutChange();
                    }
                }
            }
        }
        if(getItemViewType(position) == VIEW_TYPE_CALENDAR){
            CalendarViewHolder viewHolder = (CalendarViewHolder) currentViewHolder;
            calendar = viewHolder.getCalendar();
            noTasksInformer = viewHolder.getNoTasksInformer();
        }
    }

    public void removeItem(int position){
        new MaterialAlertDialogBuilder(calendar.getContext())
                .setTitle("Удаление задачи")
                .setMessage("Вы действительно хотите удалить эту задачу?")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.removeSilently(position - 1);
                        mItemHasBeenDeletedSnack
                                .setText("Задача была удалена")
                                .setAction(App.getInstance().getString(R.string.restore_item_dialog_item_back), view -> {
                                    int itemPos = mViewModel.restoreRemovedTask();
                                    getNoTasksInformer().setVisibility(mViewModel.getPoolSize() == 0 ? View.VISIBLE : View.GONE);
                                    notifyItemInserted(itemPos);
                                    scrollTo(itemPos);
                                }).show();
                        notifyItemRemoved(position);
                        if(mViewModel.getPoolSize() == 0){
                            getNoTasksInformer().setVisibility(View.VISIBLE);
                        }
                        else{
                            getNoTasksInformer().setVisibility(View.GONE);
                        }
                    }
                })
                .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mViewModel.getPoolSize() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_CALENDAR;
        }
        else {
            return VIEW_TYPE_TASK;
        }
    }

    public MaterialCalendarView getCalendar(){
        return calendar;
    }

    public TextView getNoTasksInformer(){
        return noTasksInformer;
    }

    public void scrollTo(int position){
        smoothScroller.setTargetPosition(position);
        mLayoutManager.startSmoothScroll(smoothScroller);
    }

    final Observer<List<TaskData>> monthChangeObserver = new Observer<List<TaskData>>() {
        @Override
        public void onChanged(List<TaskData> data) {
            mViewModel.init();
            getNoTasksInformer().setVisibility(mViewModel.getPoolSize() == 0 ? View.VISIBLE : View.GONE);
            notifyDataSetChanged();

            if(currentDate.getMonth() == CalendarDay.today().getMonth()){
                calendar.setCurrentDate(CalendarDay.today());
            }
            calendar.invalidateDecorators();
        }
    };

    public void updateData(String filterName){
        mViewModel.updateData(filterName).observe(lifecycleOwner, monthChangeObserver);
        notifyDataSetChanged();
    };

    public Snackbar getRemoveItemSnackbar(){
        return mItemHasBeenDeletedSnack;
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
