package com.example.clock.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CategoryTaskBinding;
import com.example.clock.databinding.CategoryProjectBinding;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CategoryActivitiesViewModel;
import com.example.clock.viewmodels.MainViewModel;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Date;

public class CardsListFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_UNDEFINED = 0;
    private static final int VIEW_TYPE_TASK = 1;
    private static final int VIEW_TYPE_PROJECT = 2;

    private long currentCategoryID;
    private Date selectedDay;
    private final ActivityResultLauncher<Intent> resultLauncher;
    private final CategoryActivitiesViewModel mViewModel;

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CategoryTaskBinding binding;
        private final MaterialCardView mainLayout;

        private final TextView range;
        private final ImageView important;
        private final LinearLayout categoryLayout;
        private final TextView categoryName;
        private final TextView name;
        private final TextView description;
        private final ImageView alarmImage;
        private final TextView alarmTime;

        public TaskViewHolder(CategoryTaskBinding binding) {
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

        public void bind(CategoryActivitiesViewModel vm, CategoryActivitiesViewModel.TaskObserver data){
            binding.setVm(vm);
            binding.setData(data);
            binding.executePendingBindings();
        }

        public CategoryTaskBinding getBinding() {
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

    public static class ProjectViewHolder extends RecyclerView.ViewHolder{

        private final MaterialCardView mainLayout;
        private final LinearLayout itemsLayout;

        private final TextView range;
        private final TextView name;
        private final TextView progressText;
        private final CircularProgressBar progressBar;
        private final CategoryProjectBinding binding;

        public ProjectViewHolder(CategoryProjectBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            View view = binding.getRoot();

            mainLayout = (MaterialCardView) view.findViewById(R.id.project2_top_layout);

            itemsLayout = (LinearLayout) view.findViewById(R.id.project2_children);

            name = (TextView) view.findViewById(R.id.project2_name);

            progressBar = (CircularProgressBar) view.findViewById(R.id.project2_progress);

            progressText = (TextView) view.findViewById(R.id.project2_progress_text);

            range = (TextView) view.findViewById(R.id.project2_range);
        }

        public void bind(CategoryActivitiesViewModel vm, CategoryActivitiesViewModel.ProjectObserver projObs){
            binding.setVm(vm);
            binding.setData(projObs);
            binding.executePendingBindings();
        }

        public MaterialCardView getMainLayout() {
            return mainLayout;
        }

        public LinearLayout getItemsLayout() {
            return itemsLayout;
        }

        public TextView getRange() {
            return range;
        }

        public TextView getName() {
            return name;
        }

        public TextView getProgressText() {
            return progressText;
        }

        public CircularProgressBar getProgressBar() {
            return progressBar;
        }
    }

    public CardsListFragmentAdapter(ActivityResultLauncher<Intent> resultLauncher,
                                    CategoryActivitiesViewModel viewModel) {
        mViewModel = viewModel;
        this.currentCategoryID = currentCategoryID;
        this.selectedDay = selectedDay;
        this.resultLauncher = resultLauncher;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        if (viewType == VIEW_TYPE_TASK) {
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewGroup.getContext());
            CategoryTaskBinding taskBinding =
                    CategoryTaskBinding.inflate(layoutInflater, viewGroup, false);

            return new TaskViewHolder(taskBinding);
        }
        else if(viewType == VIEW_TYPE_PROJECT){
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewGroup.getContext());
            CategoryProjectBinding projectBinding =
                    CategoryProjectBinding.inflate(layoutInflater, viewGroup, false);

            return new ProjectViewHolder(projectBinding);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder currentViewHolder, final int position) {

        if(getItemViewType(position) == VIEW_TYPE_TASK) {

            CategoryActivitiesViewModel.TaskObserver taskObs = mViewModel.getSingleTaskObs(position);
            TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

            viewHolder.bind(mViewModel, mViewModel.getSingleTaskObs(position));
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
                                            intent.putExtra("mode", "TaskEditing");
                                            intent.putExtra("ID", taskObs.getTask().getTaskId());
                                            intent.putExtra("category", taskObs.getTask().getCategoryId());

                                            resultLauncher.launch(intent);
                                            break;
                                        case 1: // Удалить

                                            AppCompatActivity act = (AppCompatActivity) view.getContext();
                                            MainViewModel viewModel = new ViewModelProvider(act)
                                                    .get(MainViewModel.class);
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
                    taskObs.setCompletedOrExpired(card.isChecked());
                }
            });

            //Colors binding
            Theme theme = mViewModel.getItemTheme(position);
            if(theme != null) {
                viewHolder.getMainLayout().setCardBackgroundColor(theme.getFirstColor());
                viewHolder.getCategoryLayout().getBackground().setTint(theme.getSecondColor());

                viewHolder.getName().setTextColor(theme.getMainTextColor());
                viewHolder.getDescription().setTextColor(theme.getMainTextColor());

                viewHolder.getRange().setTextColor(theme.getAdditionalTextColor());
                viewHolder.getAlarmTime().setTextColor(theme.getAdditionalTextColor());

                viewHolder.getImportant().setColorFilter(theme.getIconColor());
                viewHolder.getAlarmImage().setColorFilter(theme.getIconColor());
            }
        }
        else if(getItemViewType(position) == VIEW_TYPE_PROJECT){
            CategoryActivitiesViewModel.ProjectObserver projectObs = mViewModel.getProjectObs(position);

            ProjectViewHolder viewHolder = (ProjectViewHolder) currentViewHolder;

            //viewHolder.getName().setText(projectObs.getName());
            viewHolder.bind(mViewModel,projectObs);

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
                                            intent.putExtra("mode", "ProjectEditing");
                                            intent.putExtra("ID",  projectObs.getProject().getProjectId());
                                            intent.putExtra("category", projectObs.getProject().getCategoryId());

                                            resultLauncher.launch(intent);
                                            break;
                                        case 1: // Удалить

                                            AppCompatActivity act = (AppCompatActivity) view.getContext();
                                            MainViewModel viewModel = new ViewModelProvider(act)
                                                    .get(MainViewModel.class);

                                            viewModel.removeProjectByID(projectObs.getProject().getProjectId());
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
                    /*MaterialCardView card = (MaterialCardView) view;
                    card.toggle();*/
                }
            });

            Theme theme = mViewModel.getItemTheme(position);
            if(theme != null) {
                viewHolder.getMainLayout().setCardBackgroundColor(theme.getFirstColor());
                viewHolder.getProgressBar().setProgressBarColor(theme.getSecondColor());

                viewHolder.getName().setTextColor(theme.getMainTextColor());
                viewHolder.getRange().setTextColor(theme.getAdditionalTextColor());
            }

            // ---Children installation---
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewHolder.itemView.getContext());

            for(int i = 0; i < projectObs.getChildsCount(); i++){
                theme = projectObs.getChild(i).getTheme();
                CategoryTaskBinding taskBinding = CategoryTaskBinding.inflate(layoutInflater, (ViewGroup) viewHolder.itemView, false);
                taskBinding.setVm(mViewModel);
                taskBinding.setData(mViewModel.getProjectObs(position).getChild(i));

                viewHolder.getItemsLayout().addView(taskBinding.getRoot(), 0);
                taskBinding.executePendingBindings();
                taskBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
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
                                                intent.putExtra("mode", "TaskEditing");
                                                intent.putExtra("parent", projectObs.getProject().getProjectId());

                                                Task task = taskBinding.getData().getTask();
                                                intent.putExtra("ID", task.getTaskId());
                                                intent.putExtra("category", App.getSettings().getLastCategory().first);
                                                resultLauncher.launch(intent);
                                                break;
                                            case 1: // Удалить

                                                AppCompatActivity act = (AppCompatActivity) view.getContext();
                                                MainViewModel viewModel = new ViewModelProvider(act)
                                                        .get(MainViewModel.class);
                                                projectObs.removeChild(i);
                                                break;
                                        }
                                    }
                                });
                        taskOptionsDialog.show();
                        return true;
                    }
                });
                taskBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialCardView card = (MaterialCardView) view;

                        card.toggle();
                        taskBinding.getData().setCompletedOrExpired(card.isChecked());

                        projectObs.recalcProgress();

                        /*if (projectObs.getCompletedOrExpired()) {
                            viewHolder.getName().setPaintFlags(viewHolder.getName().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        else {
                            viewHolder.getName().setPaintFlags(viewHolder.getName().getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        }*/
                    }
                });

                View view = taskBinding.getRoot();
                ((MaterialCardView) view
                        .findViewById(R.id.task_top_layout))
                        .setCardBackgroundColor(theme.getFirstColor());
                ((TextView) view
                        .findViewById(R.id.task_name)).setTextColor(theme.getMainTextColor());
                ((TextView) view
                        .findViewById(R.id.task_description)).setTextColor(theme.getMainTextColor());
                ((TextView) view
                        .findViewById(R.id.task_alarm_time)).setTextColor(theme.getMainTextColor());
                ((TextView) view
                        .findViewById(R.id.task_range)).setTextColor(theme.getAdditionalTextColor());
                ((ImageView) view
                        .findViewById(R.id.task_important)).setColorFilter(theme.getAdditionalTextColor());
                ((ImageView) view
                        .findViewById(R.id.task_alarm_image)).setColorFilter(theme.getAdditionalTextColor());

            }

            FloatingActionButton fab = viewHolder.getMainLayout().findViewById(R.id.project2_add);
            fab.setColorNormal(projectObs.getTheme().getSecondColor());
            fab.setColorPressed(projectObs.getTheme().getAdditionalTextColor());
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                    intent.putExtra("mode", "TaskCreating");
                    intent.putExtra("parent", projectObs.getProject().getProjectId());
                    intent.putExtra("category", projectObs.getProject().getCategoryId());
                    resultLauncher.launch(intent);
                }
            });

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
        return mViewModel.getPoolSize();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = (Object) mViewModel.getObs(position);

        if(obj instanceof CategoryActivitiesViewModel.TaskObserver){
            return VIEW_TYPE_TASK;
        }
        if(obj instanceof CategoryActivitiesViewModel.ProjectObserver){
            return VIEW_TYPE_PROJECT;
        }
        else {
            return VIEW_TYPE_UNDEFINED;
        }
    }
}
