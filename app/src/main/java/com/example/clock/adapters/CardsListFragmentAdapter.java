package com.example.clock.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.MainViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.List;

public class CardsListFragmentAdapter extends RecyclerView.Adapter<CardsListFragmentAdapter.ViewHolder> {

    private final List<Task> tasksDataSet;
    private long currentCategoryID;
    private Date selectedDay;
    private final ActivityResultLauncher<Intent> resultLauncher;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout mainLayout;

        private final ImageView notificationImage;
        private final TextView time;
        private final TextView name;

        private final TextView listName;
        //private final TextView description;


        public ViewHolder(View view) {
            super(view);


            notificationImage = (ImageView) view.findViewById(R.id.card_notification_image);


            time = (TextView) view.findViewById(R.id.card_time);
            listName = (TextView) view.findViewById(R.id.card_category_name);
            name = (TextView) view.findViewById(R.id.card_name);
            //description = (TextView) view.findViewById(R.id.card_description);
            mainLayout = (ConstraintLayout) view.findViewById(R.id.card_constraint);
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

        public TextView getListName() {
            return listName;
        }

        /*public TextView getDescription() {
            return description;
        }*/

        public ConstraintLayout getMainLayout(){ return mainLayout; }
    }

    public CardsListFragmentAdapter(ActivityResultLauncher<Intent> resultLauncher, @NonNull List<Task> tasksDataSet) {
        this.tasksDataSet = tasksDataSet;
        this.currentCategoryID = currentCategoryID;
        this.selectedDay = selectedDay;
        this.resultLauncher = resultLauncher;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_list_item, viewGroup, false);

       ViewHolder viewH = new ViewHolder(view);

        return viewH;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that elementApp
        Task currentTask = tasksDataSet.get(position);

        viewHolder.getNotificationImage().setBackgroundResource(R.drawable.baseline_schedule_black_18);
        viewHolder.getTime().setText(currentTask.getTime24());

        viewHolder.getListName().setText("");
        viewHolder.getName().setText(currentTask.getName());
        //viewHolder.getDescription().setText(currentTask.getDescription());

        viewHolder.getMainLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Выберите действие")
                        .setItems(R.array.task_dialog_long, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasksDataSet.size();
    }
}
