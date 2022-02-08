package com.example.clock.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.fragments.CardsListFragment;
import com.example.clock.model.Category;
import com.example.clock.viewmodels.MainViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CategoriesListFragmentAdapter extends RecyclerView.Adapter<CategoriesListFragmentAdapter.ViewHolder> {

    private List<Category> categoriesDataSet;

    private Context mContext;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private MaterialToolbar mToolbar;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout mainLayout;
        private final TextView name;
        private long mCategoryID;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.category_list_name);
            mainLayout = (ConstraintLayout) view.findViewById(R.id.category_constraint);
            mCategoryID = -1;

            //mainViewModel = new ViewModelProvider(mActivity).get(MainViewModel.class);
        }

        public TextView getListNameView() {
            return name;
        }

        public ConstraintLayout getMainLayout(){
            return mainLayout;
        }

        public long getCategoryID(){
            return mCategoryID;
        }

        public void setCategoryID(long categoryID){
            mCategoryID = categoryID;
        }
    }

    public CategoriesListFragmentAdapter(Context context, ActivityResultLauncher<Intent> resultLauncher, @NonNull List<Category> categoriesDataSet) {
        this.mContext = context;
        this.categoriesDataSet = categoriesDataSet;
        this.mResultLauncher = resultLauncher;

        if(categoriesDataSet != null && categoriesDataSet.size() != 0) {
            //this.mToolbar = (MaterialToolbar) ((AppCompatActivity) context).findViewById(R.id.topAppBar);
        }
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
        viewHolder.setCategoryID(currentCategory.getCategoryId());

        viewHolder.getMainLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getSettings().setCurrentWindow(20);
                App.getSettings().setLastCategory(viewHolder.getCategoryID(), viewHolder
                        .getListNameView()
                        .getText()
                        .toString());

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment_container_view, new CardsListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        viewHolder.getMainLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Выберите действие")
                        .setItems(R.array.category_choice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0: // Изменить

                                        break;
                                    case 1: // Удалить

                                        AppCompatActivity act = (AppCompatActivity) view.getContext();
                                        MainViewModel viewModel = new ViewModelProvider(act)
                                                .get(MainViewModel.class);
                                        viewModel.removeCategoryWithItems(viewHolder.getCategoryID());
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
        if(categoriesDataSet != null){
            return categoriesDataSet.size();
        }
        else{
            return 0;
        }
    }
}
