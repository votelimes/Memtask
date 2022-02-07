package com.example.clock.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.adapters.CalendarFragmentAdapter;
import com.example.clock.adapters.SwipeableRecyclerViewTouchListener;
import com.example.clock.app.App;
import com.example.clock.databinding.FragmentCalendarBinding;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Tuple2;
import com.example.clock.viewmodels.CalendarViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.ZoneOffset;
import java.util.List;


public class CalendarFragment extends Fragment implements SearchView.OnQueryTextListener {
    RecyclerView mRecyclerView;
    CalendarFragmentAdapter mRecyclerViewAdapter;
    CalendarViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    ConstraintLayout mMainLayoutView;
    FragmentCalendarBinding binding;
    Context mContext;
    MaterialToolbar toolbar;
    SearchView searchView;
    boolean adapterCreated = false;

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 20) {
                    Toast.makeText(getActivity(), "Задача добавлена", Toast.LENGTH_SHORT).show();

                }
                else if (result.getResultCode() == 21){
                    Toast.makeText(getActivity(), "Изменения задачи применены", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Изменения отменены", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                .getActivity()
                .getApplication(),App.getDatabase(), App.getSilentDatabase());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(CalendarViewModel.class);

        binding.setVm(mViewModel);

        mRecyclerView = getView().findViewById(R.id.calendar_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_calendar_constraint);

        toolbar = getActivity().findViewById(R.id.topAppBar);

        toolbar.setTitle("Календарь активностей");

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        FloatingActionButton fabTask = getView().findViewById(R.id.fragment_calendar_fab_task);


        fabTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                taskIntent.putExtra("mode", "TaskCreating");
                taskIntent.putExtra("rangeStart", mViewModel.getSelectedDateStart().toEpochSecond(ZoneOffset.UTC) * 1000);
                activityLauncher.launch(taskIntent);
            }
        });
        mRecyclerViewAdapter = new CalendarFragmentAdapter(
                activityLauncher, mViewModel, getViewLifecycleOwner());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);


        ItemTouchHelper.SimpleCallback touchHelperCallbackRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.main_8));

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition() - 1;
                mViewModel.removeSilently(position);
                mViewModel.init();
                mRecyclerViewAdapter.getCalendar().invalidateDecorators();
                mRecyclerViewAdapter.notifyItemRemoved(viewHolder.getAbsoluteAdapterPosition());
                //mRecyclerViewAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallbackRight);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);

        //Get SearchView through MenuItem
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mViewModel.setSearchFilter(newText);
        mViewModel.filter();
        mRecyclerViewAdapter.notifyDataSetChanged();
        return false;
    }
}