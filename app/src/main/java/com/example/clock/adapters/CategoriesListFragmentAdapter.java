package com.example.clock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.model.Category;
import com.example.clock.model.Task;

import java.util.Date;
import java.util.List;

public class CategoriesListFragmentAdapter extends RecyclerView.Adapter<CategoriesListFragmentAdapter.ViewHolder> {

    private List<Category> categoriesDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.category_list_name);
        }

        public TextView getListNameView() {
            return name;
        }


    }

    public CategoriesListFragmentAdapter(@NonNull List<Category> categoriesDataSet) {
        this.categoriesDataSet = categoriesDataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_list_item, viewGroup, false);

        ViewHolder viewH = new ViewHolder(view);

        return viewH;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Category currentCategory = categoriesDataSet.get(position);

        viewHolder.getListNameView().setText(currentCategory.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(categoriesDataSet != null){
            return categoriesDataSet.size();
        }
        else{
            return 0;
        }
    }
}
