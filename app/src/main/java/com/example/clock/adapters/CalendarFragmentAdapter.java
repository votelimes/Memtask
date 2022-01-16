package com.example.clock.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CalendarTaskBinding;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.viewmodels.CalendarViewModel;
import com.example.clock.viewmodels.MainViewModel;
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

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CalendarTaskBinding binding;
        private final MaterialCardView mainLayout;

        private final TextView range;
        private final ImageView important;
        private final LinearLayout categoryLayout;
        private final TextView categoryName;
        private final TextView name;
        private final TextView description;
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
            categoryName = (TextView) view.findViewById(R.id.task_category_name);
            name = (TextView) view.findViewById(R.id.task_name);
            description = (TextView) view.findViewById(R.id.task_description);
            alarmImage = (ImageView) view.findViewById(R.id.task_alarm_image);
            alarmTime = (TextView) view.findViewById(R.id.task_alarm_time);
        }

        public void bind(CalendarViewModel vm, int pos){
            binding.setVm(vm);
            binding.setMode(CalendarViewModel.MODE_INDEPENDENTLY);
            binding.setPos(pos);
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

        public TextView getName() {
            return name;
        }

        public TextView getDescription() {
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
        MinLoadDayDecorator minLoadDecorator;
        MedLoadDayDecorator medLoadDecorator;
        HighLoadDayDecorator highLoadDecorator;
        MaxLoadDayDecorator maxLoadDecorator;
        TodayDecorator todayDecorator;

        public CalendarViewHolder(View view) {
            super(view);
            calendar = view.findViewById(R.id.calendar);
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

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            mViewModel.setDateAndUpdate(date, null);
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


        final Observer<List<TaskAndTheme>> taskPackObserver = new Observer<List<TaskAndTheme>>() {
            @Override
            public void onChanged(List<TaskAndTheme> data) {
                mViewModel.init();
                calendar.invalidateDecorators();
                //notifyDataSetChanged();

                //calendar.setCurrentDate(currentDate);
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

            Task currentTask = (Task) mViewModel.getTaskByPos(position - 1);

            TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

            viewHolder.bind(mViewModel, position - 1);
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
                                            intent.putExtra("ManagingTask", currentTask);
                                            intent.putExtra("mode", "Task");

                                            resultLauncher.launch(intent);
                                            break;
                                        case 1: // Удалить

                                            AppCompatActivity act = (AppCompatActivity) view.getContext();
                                            MainViewModel viewModel = new ViewModelProvider(act)
                                                    .get(MainViewModel.class);
                                            removeItem(currentViewHolder.getAdapterPosition());
                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });

        }
        if(getItemViewType(position) == VIEW_TYPE_CALENDAR){

        }
    }

    private void removeItem(int position){
        mViewModel.removeSilently(position);
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
}
