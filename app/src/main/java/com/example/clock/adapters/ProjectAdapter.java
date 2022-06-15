package com.example.clock.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.activities.MapActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CategoryTaskBinding;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CategoryActivitiesViewModel;
import com.example.clock.viewmodels.MemtaskViewModelBase;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.os.Handler;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.TaskViewHolder> {

    ActivityResultLauncher<Intent> mResultLauncher;
    CategoryActivitiesViewModel mViewModel;

    CardsListFragmentAdapter.ProjectViewHolder vh = null;
    int mAddedOutside = -1;
    final View rootView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.SmoothScroller smoothScroller;
    CardsListFragmentAdapter mParentAdapter;

    // data is passed into the constructor
    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnLayoutChangeListener{
        private final CategoryTaskBinding binding;
        private final MaterialCardView mainLayout;
        private final ConstraintLayout mainConstraint;

        private final TextView range;
        private final ImageView important;
        private final LinearLayout categoryLayout;
        private final TextView categoryName;
        private final EditText name;
        private final EditText description;
        private final ImageView alarmImage;
        private final TextView alarmTime;

        private Uri imageUri = null;
        private Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mainConstraint.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        public TaskViewHolder(CategoryTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View view = binding.getRoot();

            mainLayout = (MaterialCardView) view.findViewById(R.id.task_top_layout);
            mainConstraint = (ConstraintLayout) view.findViewById(R.id.main_constraint);
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

        public Uri getImageUri() {
            return imageUri;
        }

        public void setImageUri(Uri imageUri) {
            this.imageUri = imageUri;
        }

        @Override
        public void onLayoutChange(View view,
                                   int left, int top, int right, int bottom,
                                   int leftWas, int topWas, int rightWas, int bottomWas) {
            Picasso.get()
                    .load(imageUri)
                    .into(target);
        }

        public void bindLayoutChange(){
            mainLayout.addOnLayoutChangeListener(this);
        }

        public ConstraintLayout getMainConstraint() {
            return mainConstraint;
        }
    }

    public ProjectAdapter(ActivityResultLauncher<Intent> resultLauncher,
                          CategoryActivitiesViewModel viewModel, CardsListFragmentAdapter.ProjectViewHolder vh, View rootView,
                          RecyclerView.LayoutManager layoutManager,
                          CardsListFragmentAdapter adapter){

        this.mResultLauncher = resultLauncher;
        mViewModel = viewModel;
        this.vh = vh;
        this.rootView = rootView;
        mLayoutManager = layoutManager;
        smoothScroller = new LinearSmoothScroller(rootView.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        mParentAdapter = adapter;
        mAddedOutside = mParentAdapter.getProjectChildPos();
        scrollTo(mAddedOutside);
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

        CategoryActivitiesViewModel.TaskObserver taskObs = mViewModel.getProjItemObs(vh.getAbsoluteAdapterPosition(), position);
        CategoryActivitiesViewModel.ProjectObserver projectObs = mViewModel.getProjectObs(vh.getAbsoluteAdapterPosition());

        viewHolder.bind(mViewModel, taskObs);
        viewHolder.getMainConstraint().setOnLongClickListener(new View.OnLongClickListener() {
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
                                        intent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.TASK_EDITING);
                                        intent.putExtra(MemtaskViewModelBase.MTP_PARENT, projectObs.getProject().getProjectId());

                                        intent.putExtra(MemtaskViewModelBase.MTP_ID, taskObs.getTask().getTaskId());
                                        intent.putExtra(MemtaskViewModelBase.MTP_CATEGORY_ID, App.getSettings().getLastCategory().first);
                                        mResultLauncher.launch(intent);
                                        break;
                                    case 1: // Контакты

                                        break;
                                    case 2: // Адреса
                                        Intent intentMap = new Intent(view.getContext(), MapActivity.class);
                                        intentMap.putExtra(MemtaskViewModelBase.MTP_ID, taskObs.getTask().getTaskId());
                                        mResultLauncher.launch(intentMap);
                                        break;
                                    case 3: // Удалить
                                        removeItem(viewHolder.getAbsoluteAdapterPosition());
                                        break;
                                }
                            }
                        });
                taskOptionsDialog.show();
                return true;
            }
        });
        viewHolder.getMainConstraint().setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                viewHolder.getMainLayout().toggle();
                taskObs.setCompletedOrExpired(viewHolder.getMainLayout().isChecked());
                projectObs.recalcProgress();
            }

            @Override
            public void onDoubleClick(View view) {
                if(!taskObs.getCompletedOrExpired()){
                    int code = taskObs.setNotificationEnabled(view.getContext(),
                            !taskObs.getNotificationEnabled());
                    if(code == 1){
                        Toast.makeText(view.getContext(), "Не выбрано время уведомления", Toast.LENGTH_SHORT).show();
                    }
                    else if(code == 2){
                        Toast.makeText(view.getContext(), "Время уведомления уже прошло", Toast.LENGTH_SHORT).show();
                    }
                    else if(code == 0){
                        Toast.makeText(view.getContext(), "Уведомление установлено", Toast.LENGTH_SHORT).show();
                    }
                    else if(code == -1){
                        Toast.makeText(view.getContext(), "Уведомление выключено", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }));

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

            String uriString = viewHolder.getBinding().getData().getImage();
            if(uriString != null && uriString.length() != 0){
                Uri uri = Uri.parse(uriString);
                viewHolder.setImageUri(uri);
                viewHolder.bindLayoutChange();
            }
        }
        if((mAddedOutside != -1) && (mAddedOutside == viewHolder.getAbsoluteAdapterPosition())) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewHolder.getName().requestFocus();
                    InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getApplicationWindowToken(), 0);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }, 250);
            mAddedOutside = -1;
        }
    }

    public void removeItem(int pos){
            new MaterialAlertDialogBuilder(rootView.getContext())
                    .setTitle("Удаление подзадачи")
                    .setMessage("Вы действительно хотите удалить эту подзадачу?")
                    .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.removeSilentlyProjItem(vh.getAbsoluteAdapterPosition(), pos);
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
                            mParentAdapter.getRemoveItemSnackbar(snackBar);
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
        try {
            smoothScroller.setTargetPosition(position);
            mLayoutManager.startSmoothScroll(smoothScroller);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setAddedOutside(int pos){
        mAddedOutside = pos;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mViewModel.getProjectObs(vh.getAbsoluteAdapterPosition()).getChildsCount();
    }
}
