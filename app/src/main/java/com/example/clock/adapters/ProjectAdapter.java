package com.example.clock.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CategoryTaskBinding;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CategoryActivitiesViewModel;
import com.example.clock.viewmodels.MainViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.TaskViewHolder> {

    ActivityResultLauncher<Intent> mResultLauncher;
    CategoryActivitiesViewModel mViewModel;

    int mProjectPosition;
    int mAddedOutside = -1;
    final View rootView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.SmoothScroller smoothScroller;
    CardsListFragmentAdapter mParentAdapter;

    // data is passed into the constructor
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CategoryTaskBinding binding;
        private final MaterialCardView mainLayout;

        private final TextView range;
        private final ImageView important;
        private final LinearLayout categoryLayout;
        private final TextView categoryName;
        private final EditText name;
        private final EditText description;
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
            name = (EditText) view.findViewById(R.id.task_name);
            description = (EditText) view.findViewById(R.id.task_description);
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

        public EditText getName() {
            return name;
        }

        public EditText getDescription() {
            return description;
        }

        public ImageView getAlarmImage() {
            return alarmImage;
        }

        public TextView getAlarmTime() {
            return alarmTime;
        }
    }

    public ProjectAdapter(ActivityResultLauncher<Intent> resultLauncher,
                          CategoryActivitiesViewModel viewModel, int projectPosition, View rootView,
                          RecyclerView.LayoutManager layoutManager,
                          CardsListFragmentAdapter adapter){

        this.mResultLauncher = resultLauncher;
        mViewModel = viewModel;
        mProjectPosition = projectPosition;
        this.rootView = rootView;
        mLayoutManager = layoutManager;
        smoothScroller = new LinearSmoothScroller(rootView.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        mParentAdapter = adapter;
    }

    // inflates the row layout from xml when needed
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        CategoryTaskBinding taskBinding =
                CategoryTaskBinding.inflate(layoutInflater, parent, false);

        return new ProjectAdapter.TaskViewHolder(taskBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder viewHolder, int position) {

        CategoryActivitiesViewModel.TaskObserver taskObs = mViewModel.getProjItemObs(mProjectPosition, position);
        CategoryActivitiesViewModel.ProjectObserver projectObs = mViewModel.getProjectObs(mProjectPosition);

        viewHolder.bind(mViewModel, taskObs);
        viewHolder.getMainLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Выберите действие")
                        .setItems(R.array.task_dialog_short, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0: // Изменить
                                        Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                                        intent.putExtra("mode", "TaskEditing");
                                        intent.putExtra("parent", projectObs.getProject().getProjectId());

                                        intent.putExtra("ID", taskObs.getTask().getTaskId());
                                        intent.putExtra("category", App.getSettings().getLastCategory().first);
                                        mResultLauncher.launch(intent);
                                        break;
                                    case 1: // Удалить
                                        removeItem(viewHolder.getAbsoluteAdapterPosition());
                                        break;
                                }
                            }
                        });
                taskOptionsDialog.show();
                return true;
            }
        });
        viewHolder.getMainLayout().setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MaterialCardView card = (MaterialCardView) v;
                card.toggle();
                taskObs.setCompletedOrExpired(card.isChecked());
                projectObs.recalcProgress();
            }

            @Override
            public void onDoubleClick(View v) {

            }
        });

        //Colors binding
        Theme theme = projectObs.getChild(position).getTheme();
        if(theme != null) {
            viewHolder.getMainLayout().setCardBackgroundColor(theme.getFirstColor());
            viewHolder.getCategoryLayout().getBackground().setTint(theme.getSecondColor());

            viewHolder.getName().setTextColor(theme.getMainTextColor());
            viewHolder.getName().setHintTextColor(theme.getIconColor());
            viewHolder.getDescription().setTextColor(theme.getMainTextColor());

            viewHolder.getRange().setTextColor(theme.getAdditionalTextColor());
            viewHolder.getAlarmTime().setTextColor(theme.getAdditionalTextColor());

            viewHolder.getImportant().setColorFilter(theme.getIconColor());
            viewHolder.getAlarmImage().setColorFilter(theme.getIconColor());
        }
        if(mAddedOutside != -1 && mAddedOutside == viewHolder.getAbsoluteAdapterPosition()) {
            viewHolder.getName().requestFocus();
            InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public void removeItem(int pos){
            new MaterialAlertDialogBuilder(rootView.getContext())
                    .setTitle("Удаление подзадачи")
                    .setMessage("Вы действительно хотите удалить эту подзадачу?")
                    .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.removeSilentlyProjItem(mProjectPosition, pos);
                            Snackbar snackBar = mParentAdapter.getItemHasBeenDeletedDialog();
                            if(snackBar == null) {
                                snackBar = Snackbar.make(rootView, "Подзадача была удалена", mViewModel.RESTORE_ITEM_SNACKBAR_TIME);
                            }
                            else{
                                snackBar.setText("Подзадача была удалена");
                            }
                            snackBar.setAction(App.getInstance().getString(R.string.restore_item_dialog_item_back), view -> {
                                int itemPos = mViewModel.returnItemBack();
                                notifyItemInserted(itemPos);
                                scrollTo(itemPos);
                            });
                            snackBar.show();
                            mParentAdapter.setItemHasBeenDeletedDialog(snackBar);
                            notifyItemRemoved(pos);
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
    }

    public void scrollTo(int position){
        smoothScroller.setTargetPosition(position);
        mLayoutManager.startSmoothScroll(smoothScroller);
    }

    public void setAddedOutside(int pos){
        mAddedOutside = pos;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mViewModel.getProjectObs(mProjectPosition).getChildsCount();
    }
}
