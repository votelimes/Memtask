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
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseBase;
import com.example.clock.storageutils.Tuple3;
import com.example.clock.viewmodels.MainViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
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
    private final List<UserCaseBase> mainDataSet;
    private final List<Theme> themesDataSet;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout mainLayout;

        private final ImageView notificationImage;
        private final TextView time;
        private final TextView name;

        public TaskViewHolder(View view) {
            super(view);

            mainLayout = (LinearLayout) view.findViewById(R.id.card_constraint);

            notificationImage = (ImageView) view.findViewById(R.id.card_notification_image);

            time = (TextView) view.findViewById(R.id.card_time);

            name = (TextView) view.findViewById(R.id.card_name);
        }

        public ImageView getNotificationImage() {
            return notificationImage;
        }

        public TextView getTime() {
            return time;
        }

        public TextView getName() {
            return name;
        }

        public LinearLayout getMainLayout(){ return mainLayout; }
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


    public CardsListFragmentAdapter(ActivityResultLauncher<Intent> resultLauncher,
                                    Tuple3<List<Task>, List<Project>, List<Theme>> data) {
        List<Task> tasksDataSet = data.first;
        List<Project> projectsDataSet = data.second;

        mainDataSet = new ArrayList<UserCaseBase>(Math.max(tasksDataSet.size(), projectsDataSet.size()));

        for(int i = 0; i < Math.max(tasksDataSet.size(), projectsDataSet.size()); i++){
            if(i < tasksDataSet.size()){
                mainDataSet.add((UserCaseBase) tasksDataSet.get(i));
            }
            if(i < projectsDataSet.size()){
                mainDataSet.add((UserCaseBase) projectsDataSet.get(i));
            }
        }

        themesDataSet = data.third;

        this.currentCategoryID = currentCategoryID;
        this.selectedDay = selectedDay;
        this.resultLauncher = resultLauncher;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item

        if (viewType == VIEW_TYPE_TASK) {
            return new TaskViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_task, viewGroup, false));
        }
        return new ProjectViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_project, viewGroup, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder currentViewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that elementApp

        if(getItemViewType(position) == VIEW_TYPE_TASK) {

            Task currentTask = (Task) mainDataSet.get(position);

            if(currentTask.getParent() == -1){
                TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

                viewHolder.getNotificationImage().setBackgroundResource(R.drawable.baseline_schedule_black_18);
                viewHolder.getTime().setText(currentTask.getTime24());

                //viewHolder.getListName().setText("");
                viewHolder.getName().setText(currentTask.getName());
                //viewHolder.getDescription().setText(currentTask.getDescription());

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

                                                resultLauncher.launch(intent);
                                                break;
                                            case 1: // Удалить

                                                AppCompatActivity act = (AppCompatActivity) view.getContext();
                                                MainViewModel viewModel = new ViewModelProvider(act)
                                                        .get(MainViewModel.class);

                                                viewModel.removeTaskByID(currentTask.getTaskId());

                                                break;
                                        }
                                    }
                                });
                        taskOptionsDialog.show();
                        return true;
                    }
                });
            }
            else{
                currentViewHolder.itemView.setVisibility(View.GONE);
            }
        }
        else if(getItemViewType(position) == VIEW_TYPE_PROJECT){
            Project currentProject = (Project) mainDataSet.get(position);

            ProjectViewHolder viewHolder = (ProjectViewHolder) currentViewHolder;

            viewHolder.getName().setText(currentProject.getName());

            List<Task> filteredTasks = filterByParent(currentProject.getProjectId());

            AppCompatActivity activity = (AppCompatActivity) viewHolder.itemView.getContext();

            List<Task> sortedTasks = filteredTasks
                    .parallelStream()
                    .sorted(Comparator.comparingLong(Task::getTimeCreated))
                    .collect(Collectors.toList());

            sortedTasks.forEach(task -> {
                View list_item = activity
                        .getLayoutInflater()
                        .inflate(R.layout.card_project_list_item, null);
                ((TextView) list_item.findViewById(R.id.card_project_item_name)).setText(task.getmName());
                viewHolder
                        .getItemsLayout()
                        .addView(list_item);
            });

            viewHolder.getDateTime().setText(currentProject.getEndDateTime24());
        }
    }

    private List<Task> filterByParent(long parentID){
        List<Task> tasksList = new ArrayList<Task>((int) (mainDataSet.size() - mainDataSet.size() / 5));
        for (Object obj : mainDataSet ) {
            if(obj.getClass() == Task.class){
                Task task = (Task) obj;
                if(parentID == task.getParent()) {
                    tasksList.add(task);
                }
            }
        }

        return tasksList;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mainDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = (Object) mainDataSet.get(position);

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
