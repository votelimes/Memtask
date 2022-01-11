package com.example.clock.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.databinding.CategoryTaskBinding;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.MainViewModel;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CardsListFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_UNDEFINED = 0;
    private static final int VIEW_TYPE_TASK = 1;
    private static final int VIEW_TYPE_PROJECT = 2;

    private long currentCategoryID;
    private Date selectedDay;
    private final ActivityResultLauncher<Intent> resultLauncher;
    private final MainViewModel mViewModel;

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

        public void bind(MainViewModel vm, int pos){
            binding.setVm(vm);
            binding.setPos((Integer) pos);
            binding.setMode(MainViewModel.MODE_INDEPENDENTLY);
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

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView mainLayout;
        private final LinearLayout itemsLayout;

        private final TextView name;
        private final TextView progress;
        private final TextView dateTime;

        public ProjectViewHolder(View view) {
            super(view);

            mainLayout = (MaterialCardView) view.findViewById(R.id.project_top_layout);

            itemsLayout = (LinearLayout) view.findViewById(R.id.project_tasks_linear);

            name = (TextView) view.findViewById(R.id.project_name);

            progress = (TextView) view.findViewById(R.id.project_progress);

            dateTime = (TextView) view.findViewById(R.id.project_date_time);
        }

        public MaterialCardView getMainLayout() {
            return mainLayout;
        }

        public LinearLayout getItemsLayout() {
            return itemsLayout;
        }

        public TextView getName() {
            return name;
        }

        public TextView getProgress() {
            return progress;
        }

        public TextView getDateTime() {
            return dateTime;
        }

    }

    public static class Project2ViewHolder extends RecyclerView.ViewHolder{

        private final MaterialCardView mainLayout;
        private final LinearLayout itemsLayout;


        private final TextView range;
        private final TextView name;
        private final TextView progressText;
        private final CircularProgressBar progressBar;

        public Project2ViewHolder(View view) {
            super(view);

            mainLayout = (MaterialCardView) view.findViewById(R.id.project2_top_layout);

            itemsLayout = (LinearLayout) view.findViewById(R.id.project2_children);

            name = (TextView) view.findViewById(R.id.project2_name);

            progressBar = (CircularProgressBar) view.findViewById(R.id.project2_progress);

            progressText = (TextView) view.findViewById(R.id.project2_progress_text);

            range = (TextView) view.findViewById(R.id.project2_range);
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
                                    MainViewModel viewModel) {
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
        // DEF PROJECT
        else if(viewType == VIEW_TYPE_PROJECT && false){
            return new ProjectViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_project, viewGroup, false));
        }
        // NEW PROJECT2
        else if(viewType == VIEW_TYPE_PROJECT){
            return new Project2ViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.project2, viewGroup, false));
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder currentViewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that elementApp

        if(getItemViewType(position) == VIEW_TYPE_TASK) {

            Task currentTask = (Task) mViewModel.getByPos(position);

            if(currentTask.getParentID().equals("")){
                TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

                viewHolder.bind(mViewModel, position);
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
                                                removeItem(MainViewModel.MODE_INDEPENDENTLY, currentViewHolder.getAdapterPosition());
                                                break;
                                        }
                                    }
                                });
                        taskOptionsDialog.show();
                        return true;
                    }
                });
            }

        }

        else if(getItemViewType(position) == VIEW_TYPE_PROJECT){
            Project currentProject = (Project) mViewModel.getByPos(position);

            Project2ViewHolder viewHolder = (Project2ViewHolder) currentViewHolder;

            viewHolder.getName().setText(currentProject.getName());

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
                                            intent.putExtra("ManagingProject", currentProject);
                                            intent.putExtra("mode", "Project");

                                            resultLauncher.launch(intent);
                                            break;
                                        case 1: // Удалить

                                            AppCompatActivity act = (AppCompatActivity) view.getContext();
                                            MainViewModel viewModel = new ViewModelProvider(act)
                                                    .get(MainViewModel.class);

                                            viewModel.removeProjectByID(currentProject.getProjectId());
                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });

            List<Task> filteredTasks = mViewModel.getTasksByProject(currentProject.getProjectId());
            AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();

            List<Task> sortedTasks = filteredTasks
                    .parallelStream()
                    .sorted(Comparator.comparingLong(Task::getTimeCreated))
                    .collect(Collectors.toList());
            // ---Children installation---
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewHolder.itemView.getContext());

            for(int i = 0; i < sortedTasks.size(); i++){
                CategoryTaskBinding taskBinding = CategoryTaskBinding.inflate(layoutInflater, (ViewGroup) viewHolder.itemView, false);
                taskBinding.setVm(mViewModel);
                taskBinding.setPos(i);
                taskBinding.setMode(MainViewModel.MODE_PROJECT_ITEM);
                viewHolder.getItemsLayout().addView(taskBinding.getRoot(), 0);
                taskBinding.executePendingBindings();
            }
            FloatingActionButton fab = viewHolder.getMainLayout().findViewById(R.id.project2_add);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    private void removeItem(int mode, int position){
        mViewModel.removeSilently(mode, position);
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
        Object obj = (Object) mViewModel.getByPos(position);

        if(obj.getClass() == Task.class){
            return VIEW_TYPE_TASK;
        }
        if(obj.getClass() == Project.class){
            return VIEW_TYPE_PROJECT;
        }
        else {
            return VIEW_TYPE_UNDEFINED;
        }
    }


}
