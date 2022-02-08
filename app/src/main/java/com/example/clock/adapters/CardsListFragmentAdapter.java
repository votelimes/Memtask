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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CategoryTaskBinding;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CategoryActivitiesViewModel;
import com.example.clock.viewmodels.MainViewModel;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Date;
import java.util.List;

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

        public void bind(CategoryActivitiesViewModel vm, int pos){
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

            Task currentTask = (Task) mViewModel.getItem(mViewModel.MODE_INDEPENDENTLY, position);

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
                                                intent.putExtra("mode", "TaskEditing");
                                                intent.putExtra("ID", currentTask.getTaskId());
                                                intent.putExtra("category", currentTask.getCategoryId());

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

                //Colors binding
                Theme theme = mViewModel.getSingleItemTheme(position);
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

        }
        else if(getItemViewType(position) == VIEW_TYPE_PROJECT){
            Project currentProject = (Project) mViewModel.getItem(mViewModel.MODE_INDEPENDENTLY, position);

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
                                            intent.putExtra("mode", "ProjectEditing");
                                            intent.putExtra("ID",  currentProject.getProjectId());
                                            intent.putExtra("category", currentProject.getCategoryId());

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

            Theme theme = mViewModel.getSingleItemTheme(position);
            if(theme != null) {
                viewHolder.getMainLayout().setCardBackgroundColor(theme.getFirstColor());
                viewHolder.getProgressBar().setProgressBarColor(theme.getSecondColor());

                viewHolder.getName().setTextColor(theme.getMainTextColor());
                viewHolder.getRange().setTextColor(theme.getAdditionalTextColor());
            }

            List<TaskAndTheme> filteredTasks = mViewModel.getAllProjectTasks();
            AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();

            /*List<Task> sortedTasks = filteredTasks
                    .stream()
                    .sorted(Comparator.comparingLong(TaskAndTheme::getTimeCreated))
                    .collect(Collectors.toList());*/


            // ---Children installation---
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewHolder.itemView.getContext());
            int progress = 0;
            for(int i = 0; i < filteredTasks.size(); i++){
                if(filteredTasks.get(i).task.getParentID().equals(currentProject.getProjectId())) {
                    theme = filteredTasks.get(i).theme;
                    CategoryTaskBinding taskBinding = CategoryTaskBinding.inflate(layoutInflater, (ViewGroup) viewHolder.itemView, false);
                    taskBinding.setVm(mViewModel);
                    taskBinding.setPos(i);
                    taskBinding.setMode(MainViewModel.MODE_PROJECT_ITEM);
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
                                                    intent.putExtra("parent", currentProject.getProjectId());
                                                    int pos = taskBinding.getPos();
                                                    Task task = taskBinding.getVm().getProjItem(pos);
                                                    String id = taskBinding.getVm().getProjItem(pos).getTaskId();
                                                    intent.putExtra("ID", taskBinding.getVm().getProjItem(pos).getTaskId());
                                                    intent.putExtra("category", App.getSettings().getLastCategory().first);
                                                    resultLauncher.launch(intent);
                                                    break;
                                                case 1: // Удалить

                                                    AppCompatActivity act = (AppCompatActivity) view.getContext();
                                                    MainViewModel viewModel = new ViewModelProvider(act)
                                                            .get(MainViewModel.class);
                                                    removeItem(MainViewModel.MODE_PROJECT_ITEM, taskBinding.getPos());
                                                    break;
                                            }
                                        }
                                    });
                            taskOptionsDialog.show();
                            return true;
                        }
                    });
                    if (filteredTasks.get(i).task.isCompleted()) {
                        progress++;
                    }
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

            }
            if(filteredTasks.size() != 0) {
                viewHolder.getProgressBar().setProgress((100 / filteredTasks.size()) * progress);
                viewHolder.getProgressText()
                        .setText(String.valueOf((int)((100 / filteredTasks.size()) * progress)) + "%");
            }
            else{
                viewHolder.getProgressBar().setProgress(0);
                viewHolder.getProgressText().setText("0%");
            }

            FloatingActionButton fab = viewHolder.getMainLayout().findViewById(R.id.project2_add);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                    intent.putExtra("mode", "TaskCreating");
                    intent.putExtra("parent", currentProject.getProjectId());
                    intent.putExtra("category", currentProject.getCategoryId());
                    resultLauncher.launch(intent);
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
        Object obj = (Object) mViewModel.getItem(mViewModel.MODE_INDEPENDENTLY, position);

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
