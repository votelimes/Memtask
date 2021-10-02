package com.example.clock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.model.Category;
import com.example.clock.model.Task;

import java.util.List;

public class DefaultFragmentListAdapter extends RecyclerView.Adapter<DefaultFragmentListAdapter.ViewHolder> {

    private List<Task> tasksDataSet;
    private List<Category> categoriesDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView notificationImage;
        private final TextView time;
        private final TextView listName;
        private final TextView description;

        public ViewHolder(View view) {
            super(view);

            notificationImage = (ImageView) view.findViewById(R.id.card_notification_image);

            time = (TextView) view.findViewById(R.id.card_time);
            listName = (TextView) view.findViewById(R.id.card_list_name);
            description = (TextView) view.findViewById(R.id.card_description);
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

    public DefaultFragmentListAdapter(List<Task> tasksDataSet, List<Category> categoriesDataSet) {
        this.tasksDataSet = tasksDataSet;
        this.categoriesDataSet = categoriesDataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_note, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that elementApp
        Task currentTask = tasksDataSet.get(position);

        viewHolder.getNotificationImage().setBackgroundResource(R.drawable.baseline_schedule_black_18);
        viewHolder.getTime().setText(currentTask.getTime24());
        for(Category category : categoriesDataSet){
            if(category.getCategoryId() == currentTask.getCategoryId()){
                viewHolder.getListName().setText(category.getName());
                break;
            }
        }
        viewHolder.getDescription().setText(currentTask.getDescription());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasksDataSet.size();
    }
}
