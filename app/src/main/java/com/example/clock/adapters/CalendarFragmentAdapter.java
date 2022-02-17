package com.example.clock.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CalendarTaskBinding;
import com.example.clock.model.TaskData;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CalendarViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.LocalDate;

import java.util.Date;
import java.util.List;

public class CalendarFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_UNDEFINED = 0;
    private static final int VIEW_TYPE_TASK = 1;
    private static final int VIEW_TYPE_CALENDAR = 2;

    private Date selectedDay;
    private final ActivityResultLauncher<Intent> resultLauncher;
    private final CalendarViewModel mViewModel;
    private MaterialCalendarView calendar;
    private LifecycleOwner lifecycleOwner;
    private CalendarDay currentDate;
    private TextView noTasksInformer;

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CalendarTaskBinding binding;
        private final MaterialCardView mainLayout;

        private final TextView range;
        private final ImageView important;

        private final LinearLayout categoryLayout;
        private final Drawable categoryLayoutDrawable;
        private final TextView categoryName;
        private final EditText name;
        private final EditText description;
        private final ImageView alarmImage;
        private final TextView alarmTime;

        public TaskViewHolder(CalendarTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View view = binding.getRoot();

            mainLayout = (MaterialCardView) view.findViewById(R.id.task_top_layout);
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
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder implements OnDateSelectedListener, OnRangeSelectedListener, OnMonthChangedListener {
        MaterialCalendarView calendar;
        TextView noTasksInformer;
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
            currentDate = CalendarDay.today();
            calendar.setCurrentDate(currentDate);
            calendar.addDecorators(todayDecorator, minLoadDecorator, medLoadDecorator,
                    highLoadDecorator, maxLoadDecorator);
            calendar.setOnDateChangedListener(this);
            calendar.setOnRangeSelectedListener(this);
            calendar.setOnMonthChangedListener(this);
            mViewModel.updateMonthTasksPack().observe(lifecycleOwner, taskPackObserver);
        }
        public MaterialCalendarView getCalendar(){
            return calendar;
        }
        public TextView getNoTasksInformer() {
            return noTasksInformer;
        }

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            mViewModel.setDateAndUpdate(date, null);
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
            widget.setSelectedDate(date);
            currentDate = date;
            mViewModel.setDate(date, null);
            mViewModel.requestMonthTasksPack().removeObservers(lifecycleOwner);
            mViewModel.updateMonthTasksPack().observe(lifecycleOwner, taskPackObserver);
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


        final Observer<List<TaskData>> taskPackObserver = new Observer<List<TaskData>>() {
            @Override
            public void onChanged(List<TaskData> data) {
                mViewModel.init();
                getNoTasksInformer().setVisibility(mViewModel.getPoolSize() == 0 ? View.VISIBLE : View.GONE);
                notifyDataSetChanged();
                calendar.invalidateDecorators();
            }
        };
    }

    public CalendarFragmentAdapter(ActivityResultLauncher<Intent> resultLauncher,
                                   CalendarViewModel viewModel, LifecycleOwner lcowner) {
        mViewModel = viewModel;
        this.selectedDay = selectedDay;
        this.resultLauncher = resultLauncher;
        this.lifecycleOwner = lcowner;
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
            CalendarViewModel.TaskObserver taskData = mViewModel.getTaskObserver(position - 1);

            TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

            viewHolder.bind(mViewModel, taskData);
            viewHolder.getMainLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Выберите действие")
                            .setItems(R.array.task_dialog_long, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0: // Изменить
                                            Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                                            intent.putExtra("ID", taskData.getTask().getTaskId());
                                            intent.putExtra("mode", "TaskEditing");

                                            /*long rangeStart = calendar
                                                    .getSelectedDate()
                                                    .getDate()
                                                    .toEpochDay() * 24 * 60 * 60;
                                            if(rangeStart == -1 || rangeStart == 0){
                                                rangeStart = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                                            }
                                            intent.putExtra("rangeStart", rangeStart);*/

                                            resultLauncher.launch(intent);
                                            break;
                                        case 1: // Удалить
                                            removeItem(currentViewHolder.getAbsoluteAdapterPosition());
                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });
            viewHolder.getMainLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialCardView card = (MaterialCardView) view;
                    card.toggle();
                    viewHolder.getBinding().getData().setCompleted(card.isChecked());
                }
            });

            //Colors binding
            Theme theme = taskData.getTheme();
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

            }
        }
        if(getItemViewType(position) == VIEW_TYPE_CALENDAR){
            CalendarViewHolder viewHolder = (CalendarViewHolder) currentViewHolder;
            calendar = viewHolder.getCalendar();
            noTasksInformer = viewHolder.getNoTasksInformer();
        }
    }

    private void removeItem(int position){
        mViewModel.removeSilently(position - 1);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mViewModel.getPoolSize());
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
}
