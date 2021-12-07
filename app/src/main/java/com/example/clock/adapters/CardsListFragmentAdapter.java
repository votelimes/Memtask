package com.example.clock.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.model.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.List;

public class CardsListFragmentAdapter extends RecyclerView.Adapter<CardsListFragmentAdapter.ViewHolder> {

    private List<Task> tasksDataSet;
    private long currentCategoryID;
    private Date selectedDay;

    private Activity mActivity;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView notificationImage;
        private final TextView time;
        private final TextView listName;
        private final TextView description;

        private final AppCompatActivity mActivity;

        public ViewHolder(View view, Context context) {
            super(view);

            mActivity = (AppCompatActivity) context;

            notificationImage = (ImageView) view.findViewById(R.id.card_notification_image);

            time = (TextView) view.findViewById(R.id.card_time);
            listName = (TextView) view.findViewById(R.id.card_list_name);
            description = (TextView) view.findViewById(R.id.card_description);

            view.findViewById(R.id.card_constraint).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(mActivity)
                            .setTitle("Выберите действие")
                            .setItems(R.array.task_dialog_long, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i){
                                        case 0: // Изменить


                                            break;
                                        case 1: // Удалить

                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });
        }

        public ImageView getNotificationImage() {
            return notificationImage;
        }

        public TextView getTime() {
            return time;
        }

        public TextView getListName() {
            return listName;
        }

        public TextView getDescription() {
            return description;
        }
    }

    public CardsListFragmentAdapter(Context context, ActivityResultLauncher resultLauncher, @NonNull List<Task> tasksDataSet,
                                    @NonNull long currentCategoryID, Date selectedDay) {
        this.tasksDataSet = tasksDataSet;
        this.currentCategoryID = currentCategoryID;
        this.selectedDay = selectedDay;
        this.mActivity =  (Activity) context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_list_item, viewGroup, false);

       ViewHolder viewH = new ViewHolder(view, mActivity);

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

        /*if (this.selectedDay != null){
            Calendar selectedDate = GregorianCalendar.getInstance();
            selectedDate.setTimeInMillis(this.selectedDay.getTime());
            selectedDate.set(Calendar.HOUR_OF_DAY, 1); // 0 - 23

            Calendar taskDate = GregorianCalendar.getInstance();


            for (Task task : tasksDataSet) {
                taskDate.setTimeInMillis(task.getTimeInMillis());
                if (selectedDate.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR)
                    && selectedDate.get(Calendar.MONTH) == taskDate.get(Calendar.MONTH)
                    && selectedDate.get(Calendar.DAY_OF_MONTH) == taskDate.get(Calendar.DAY_OF_MONTH)) {

                    viewHolder.getListName().setText(task.getCategoryName());
                    break;
                }
            }
        }*/

        viewHolder.getListName().setText(currentTask.getCategoryName());
        viewHolder.getDescription().setText(currentTask.getDescription());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasksDataSet.size();
    }
}
