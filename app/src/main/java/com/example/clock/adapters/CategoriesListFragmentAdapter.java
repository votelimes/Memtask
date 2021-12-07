package com.example.clock.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.fragments.CardsListFragment;
import com.example.clock.model.Category;
import com.example.clock.viewmodels.MainViewModel;

import java.util.List;

public class CategoriesListFragmentAdapter extends RecyclerView.Adapter<CategoriesListFragmentAdapter.ViewHolder> {

    private List<Category> categoriesDataSet;

    private Context mContext;
    private ActivityResultLauncher mResultLauncher;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final AppCompatActivity mActivity;
        private final MainViewModel mainViewModel;

        private long mCategoryID;

        public ViewHolder(View view, Context context) {
            super(view);

            name = (TextView) view.findViewById(R.id.category_list_name);
            mActivity = (AppCompatActivity) context;

            mainViewModel = new ViewModelProvider(mActivity).get(MainViewModel.class);

            view.findViewById(R.id.category_constraint).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getSettings().setCurrentWindow(2);
                    App.getSettings().setLastCategoryID(mCategoryID);

                    mActivity.getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.main_fragment_container_view, new CardsListFragment())
                            .addToBackStack(null)
                            .commit();

                    //TextView textView = (TextView) view.findViewById(R.id.category_list_name);
                    //Log.d("CATEGORY:NAME:: ",textView.getText().toString());
                    /*Log.d("CATEGORY:ID:: ",String.valueOf(mCategoryID));*/
                }
            });
        }

        public TextView getListNameView() {
            return name;
        }

        public long getCategoryID() {
            return mCategoryID;
        }

        public void setCategoryID(long categoryID) {
            this.mCategoryID = categoryID;
        }

    }

    public CategoriesListFragmentAdapter(Context context, ActivityResultLauncher resultLauncher, @NonNull List<Category> categoriesDataSet) {
        this.mContext = context;
        this.categoriesDataSet = categoriesDataSet;
        this.mResultLauncher = resultLauncher;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_list_item, viewGroup, false);

        ViewHolder viewH = new ViewHolder(view, mContext);
        return viewH;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Category currentCategory = categoriesDataSet.get(position);
        viewHolder.getListNameView().setText(currentCategory.getName());
        viewHolder.setCategoryID(categoriesDataSet.get(position).getCategoryId());
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
