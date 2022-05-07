package com.example.clock.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.example.clock.databinding.CategoryProjectBinding;
import com.example.clock.databinding.CategoryTaskBinding;
import com.example.clock.model.Theme;
import com.example.clock.viewmodels.CategoryActivitiesViewModel;
import com.example.clock.viewmodels.MemtaskViewModelBase;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class CardsListFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //TODO: PROJECT BG IMAGE intinite_loop?

    private static final int VIEW_TYPE_UNDEFINED = 0;
    private static final int VIEW_TYPE_TASK = 1;
    private static final int VIEW_TYPE_PROJECT = 2;

    private final ActivityResultLauncher<Intent> resultLauncher;
    private final CategoryActivitiesViewModel mViewModel;
    private final View rootView;
    private final RecyclerView.LayoutManager mLayoutManager;
    private int mAddedOutside = -1;
    private Snackbar mItemHasBeenDeletedSnack;

    RecyclerView.SmoothScroller smoothScroller;

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
        private final ImageView bgImage;

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
            bgImage = (ImageView) view.findViewById(R.id.bg_image);
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

        public ImageView getBgImage(){
            return bgImage;
        }
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder{

        private final MaterialCardView mainLayout;

        private final TextView range;
        private final EditText name;
        private final TextView progressText;
        private final CircularProgressBar progressBar;

        private final CategoryProjectBinding binding;
        private final RecyclerView.LayoutManager mLayoutManager;

        private final RecyclerView recyclerView;

        private ProjectAdapter mAdapter;
        private final ImageView bgImage;

        public ProjectViewHolder(CategoryProjectBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            View view = binding.getRoot();

            mainLayout = (MaterialCardView) view.findViewById(R.id.project2_top_layout);

            name = (EditText) view.findViewById(R.id.project2_name);

            progressBar = (CircularProgressBar) view.findViewById(R.id.project2_progress);

            progressText = (TextView) view.findViewById(R.id.project2_progress_text);

            range = (TextView) view.findViewById(R.id.project2_range);

            recyclerView = (RecyclerView) view.findViewById(R.id.child_holder);

            mLayoutManager = new LinearLayoutManager(view.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(mLayoutManager);

            bgImage = (ImageView) view.findViewById(R.id.bg_image);
        }

        public void bind(CategoryActivitiesViewModel vm, CategoryActivitiesViewModel.ProjectObserver projObs){
            binding.setVm(vm);
            binding.setData(projObs);
            binding.executePendingBindings();
        }

        public MaterialCardView getMainLayout() {
            return mainLayout;
        }

        public TextView getRange() {
            return range;
        }

        public EditText getName() {
            return name;
        }

        public TextView getProgressText() {
            return progressText;
        }

        public CircularProgressBar getProgressBar() {
            return progressBar;
        }

        public ProjectAdapter getAdapter() {
            return mAdapter;
        }

        public void setAdapter(ProjectAdapter adapter) {
            this.mAdapter = adapter;
            recyclerView.setAdapter(mAdapter);
        }

        public RecyclerView.LayoutManager getLayoutManager(){
            return mLayoutManager;
        }

        public CategoryProjectBinding getBinding() {
            return binding;
        }

        public RecyclerView getRecyclerView() {
            return recyclerView;
        }

        public ImageView getBgImage(){
            return bgImage;
        }
    }

    public CardsListFragmentAdapter(ActivityResultLauncher<Intent> resultLauncher,
                                    CategoryActivitiesViewModel viewModel, View rootView, RecyclerView.LayoutManager layoutManager) {
        App.getInstance().dropLoadTimer();
        mViewModel = viewModel;
        this.resultLauncher = resultLauncher;
        this.rootView = rootView;
        mLayoutManager = layoutManager;
        smoothScroller = new LinearSmoothScroller(rootView.getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        mItemHasBeenDeletedSnack = Snackbar.make(rootView, "", mViewModel.RESTORE_ITEM_SNACKBAR_TIME);

        View snackBarView = mItemHasBeenDeletedSnack.getView();
        snackBarView.setTranslationY(-(convertDpToPixel(80, snackBarView.getContext())));
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
            LayoutInflater layoutInflater =
                    LayoutInflater.from(viewGroup.getContext());
            CategoryProjectBinding projectBinding =
                    CategoryProjectBinding.inflate(layoutInflater, viewGroup, false);

            return new ProjectViewHolder(projectBinding);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder currentViewHolder, final int position) {

        if(getItemViewType(position) == VIEW_TYPE_TASK) {

            CategoryActivitiesViewModel.TaskObserver taskObs = mViewModel.getSingleTaskObs(position);
            TaskViewHolder viewHolder = (TaskViewHolder) currentViewHolder;

            viewHolder.bind(mViewModel, mViewModel.getSingleTaskObs(position));
            viewHolder.getMainLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Выберите действие")
                            .setItems(R.array.task_dialog_long, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0: // Декомпозировать
                                            break;
                                        case 1: // Изменить
                                            Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                                            intent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.TASK_EDITING);
                                            intent.putExtra(MemtaskViewModelBase.MTP_ID, taskObs.getTask().getTaskId());
                                            intent.putExtra(MemtaskViewModelBase.MTP_CATEGORY_ID, taskObs.getTask().getCategoryId());

                                            resultLauncher.launch(intent);
                                            break;
                                        case 2: // Удалить
                                            removeItem(currentViewHolder.getAbsoluteAdapterPosition());
                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });

            viewHolder.getMainLayout().setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    MaterialCardView card = (MaterialCardView) view;
                    card.toggle();
                    taskObs.setCompletedOrExpired(card.isChecked());
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
            Theme theme = mViewModel.getItemTheme(position);
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

                    viewHolder.getMainLayout()
                            .getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    int width = viewHolder.getMainLayout().getWidth();
                                    int height = viewHolder.getMainLayout().getHeight();

                                    Picasso.get()
                                            .load(uri)
                                            .resize(width, height)
                                            .into(viewHolder.getBgImage());
                                }
                            });
                }
            }
            if(mAddedOutside != -1 && mAddedOutside == viewHolder.getAbsoluteAdapterPosition()) {
                viewHolder.getName().requestFocus();
                InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
        else if(getItemViewType(position) == VIEW_TYPE_PROJECT){
            CategoryActivitiesViewModel.ProjectObserver projectObs = mViewModel.getProjectObs(position);

            ProjectViewHolder viewHolder = (ProjectViewHolder) currentViewHolder;

            viewHolder.bind(mViewModel,projectObs);

            viewHolder.getMainLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MaterialAlertDialogBuilder taskOptionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Выберите действие")
                            .setItems(R.array.project_dialog_long, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            mViewModel.addProjectChild(viewHolder.getAbsoluteAdapterPosition());
                                            notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                                            viewHolder.getAdapter().scrollTo(viewHolder.mAdapter.getItemCount());
                                            viewHolder.getAdapter().setAddedOutside(viewHolder.getAbsoluteAdapterPosition());
                                            break;

                                        case 1: // Изменить
                                            Intent intent = new Intent(view.getContext(), ManageTaskActivity.class);
                                            intent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.PROJECT_EDITING);
                                            intent.putExtra(MemtaskViewModelBase.MTP_ID,  projectObs.getProject().getProjectId());
                                            intent.putExtra(MemtaskViewModelBase.MTP_CATEGORY_ID, projectObs.getProject().getCategoryId());

                                            resultLauncher.launch(intent);
                                            break;
                                        case 2: // Удалить
                                            removeItem(viewHolder.getAbsoluteAdapterPosition());
                                            break;
                                    }
                                }
                            });
                    taskOptionsDialog.show();
                    return true;
                }
            });
            viewHolder.getMainLayout().setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {

                }

                @Override
                public void onDoubleClick(View v) {
                    mViewModel.addProjectChild(viewHolder.getAbsoluteAdapterPosition());
                    Toast.makeText(v.getContext(), "Подзадача добавлена", Toast.LENGTH_SHORT).show();
                    projectObs.recalcProgress();
                    viewHolder.getAdapter().notifyDataSetChanged();
                    viewHolder.getAdapter().scrollTo(viewHolder.mAdapter.getItemCount());
                    viewHolder.getAdapter().setAddedOutside(viewHolder.getAbsoluteAdapterPosition());
                }
            }));

            Theme theme = mViewModel.getItemTheme(position);
            if(theme != null) {
                viewHolder.getMainLayout().setCardBackgroundColor(theme.getFirstColor());
                viewHolder.getProgressBar().setProgressBarColor(theme.getSecondColor());
                viewHolder.getProgressBar().setBackgroundColor(theme.getIconColor());

                viewHolder.getName().setTextColor(theme.getMainTextColor());
                viewHolder.getName().setHintTextColor(theme.getIconColor());
                viewHolder.getRange().setTextColor(theme.getAdditionalTextColor());

                String uriString = viewHolder.getBinding().getData().getImage();
                if(uriString != null && uriString.length() != 0){
                    Uri uri = Uri.parse(uriString);

                    viewHolder.getMainLayout()
                            .getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    int width = viewHolder.getMainLayout().getWidth();
                                    int height = viewHolder.getMainLayout().getHeight();

                                    Picasso.get()
                                            .load(uri)
                                            .resize(width, height)
                                            .into(viewHolder.getBgImage());
                                }
                            });
                }
            }

            ProjectAdapter adapter = new ProjectAdapter(resultLauncher, mViewModel, currentViewHolder.getAbsoluteAdapterPosition(),
                    rootView, viewHolder.getLayoutManager(), CardsListFragmentAdapter.this);

            viewHolder.setAdapter(adapter);

            if(mAddedOutside != -1 && mAddedOutside == viewHolder.getAbsoluteAdapterPosition()) {
                viewHolder.getName().requestFocus();
                InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
        if(App.isTesting()){
            App.getInstance().fixLoadTimer();
        }
    }

    public void removeItem(int position){
        if(getItemViewType(position) == VIEW_TYPE_TASK){
            new MaterialAlertDialogBuilder(rootView.getContext())
                    .setTitle("Удаление задачи")
                    .setMessage("Вы действительно хотите удалить эту задачу?")
                    .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.removeSilently(position);
                            mItemHasBeenDeletedSnack
                                    .setText("Задача была удалена")
                                    .setAction(App.getInstance().getString(R.string.restore_item_dialog_item_back), view -> {
                                        int itemPos = mViewModel.returnItemBack();
                                        notifyItemInserted(itemPos);
                                        scrollTo(itemPos);
                                    }).show();
                            notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
        else{
            new MaterialAlertDialogBuilder(rootView.getContext())
                    .setTitle("Удаление проекта")
                    .setMessage("Вы дейстсительно хотите удалить этот проект?")
                    .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mViewModel.removeSilently(position);

                            mItemHasBeenDeletedSnack
                                    .setText("Проект был удален")
                                    .setAction(App.getInstance().getString(R.string.restore_item_dialog_item_back), view -> {
                                        int itemPos = mViewModel.returnItemBack();
                                        notifyItemInserted(itemPos);
                                        smoothScroller.setTargetPosition(itemPos);
                                        mLayoutManager.startSmoothScroll(smoothScroller);
                                    })
                                    .show();
                            notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            notifyDataSetChanged();
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void scrollTo(int position){
        smoothScroller.setTargetPosition(position);
        mLayoutManager.startSmoothScroll(smoothScroller);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mViewModel.getPoolSize();
    }

    @Override
    public int getItemViewType(int position) {
        CategoryActivitiesViewModel.ParentObserver obs = mViewModel.getObs(position);

        if(obs instanceof CategoryActivitiesViewModel.TaskObserver){
            return VIEW_TYPE_TASK;
        }
        if(obs instanceof CategoryActivitiesViewModel.ProjectObserver){
            return VIEW_TYPE_PROJECT;
        }
        else {
            return VIEW_TYPE_UNDEFINED;
        }
    }

    public void setAddedOutside(int pos){
        mAddedOutside = pos;
    }

    public Snackbar getItemHasBeenDeletedDialog(){
        return mItemHasBeenDeletedSnack;
    }

    public void getRemoveItemSnackbar(Snackbar snackbar){
        mItemHasBeenDeletedSnack = snackbar;
    }

    public String imageUriToPath(Uri uri){
        List<String> ps = uri.getPathSegments();

        int startIndex = 0;

        for (int i = 0; i < ps.size(); i++){
            if(ps.get(i).contains("content")){
                startIndex = i;
                break;
            }
        }

        String result = ps.get(startIndex).replace("content://", "");

        for (int i = 0; i < ps.size(); i++){
            if(i > startIndex){
                result += "/" +ps.get(i);
            }
        }
        //String path = ImageFilePath.getPath(rootView.getContext(), uri);
        //String path = RealPathUtil.getRealPathFromURI_API19(rootView.getContext(), uri);

        File file = new File(result);
        boolean exists = file.exists();

        return result;
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
