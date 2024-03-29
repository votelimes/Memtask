package com.votelimes.memtask.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.votelimes.memtask.R;
import com.votelimes.memtask.activities.ManageTaskActivity;
import com.votelimes.memtask.adapters.CalendarFragmentAdapter;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.databinding.FragmentCalendarBinding;
import com.votelimes.memtask.viewmodels.CalendarViewModel;
import com.votelimes.memtask.viewmodels.MemtaskViewModelBase;
import com.votelimes.memtask.viewmodels.ViewModelFactoryBase;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.ZoneOffset;


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

        toolbar = getActivity().findViewById(R.id.toolbar);

        toolbar.getMenu().findItem(R.id.action_search).setVisible(true);

        toolbar.setTitle("Календарь активностей");

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        FloatingActionButton fabTask = getView().findViewById(R.id.fragment_calendar_fab_task);


        fabTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                taskIntent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.TASK_CREATING);
                taskIntent.putExtra(MemtaskViewModelBase.MTP_RANGE_START, mViewModel.getSelectedDateStart().toEpochSecond(ZoneOffset.UTC) * 1000);
                activityLauncher.launch(taskIntent);
            }
        });

        setUpRecyclerView();

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);

        //Get SearchView through MenuItem
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    protected void setUpRecyclerView(){
        mRecyclerViewAdapter = new CalendarFragmentAdapter(
                activityLauncher, mViewModel, getViewLifecycleOwner(), getView(), mLayoutManager);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                mRecyclerView.requestFocus();
                return false;
            }
        });

        ItemTouchHelper.SimpleCallback touchHelperCallbackRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mRecyclerViewAdapter.removeItem(viewHolder.getAbsoluteAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallbackRight);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        ItemTouchHelper.SimpleCallback touchHelperCallbackLeft = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Intent taskIntent = new Intent(getView().getContext(), ManageTaskActivity.class);
                CalendarFragmentAdapter.TaskViewHolder vh = (CalendarFragmentAdapter.TaskViewHolder) viewHolder;
                taskIntent.putExtra(MemtaskViewModelBase.MTP_ID, vh.getBinding().getData().getTask().getTaskId());
                taskIntent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.TASK_EDITING);
                taskIntent.putExtra(MemtaskViewModelBase.MTP_RANGE_START, mViewModel.getSelectedDateStart().toEpochSecond(ZoneOffset.UTC) * 1000);
                activityLauncher.launch(taskIntent);
                CalendarFragmentAdapter adapter = (CalendarFragmentAdapter) vh.getBindingAdapter();
                adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
            }
        };
        itemTouchHelper = new ItemTouchHelper(touchHelperCallbackLeft);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mRecyclerViewAdapter.getRemoveItemSnackbar().dismiss();
        mRecyclerViewAdapter.updateData(newText);
        return false;
    }

    @Override
    public void onViewStateRestored(Bundle inState) {
        super.onViewStateRestored(inState);
        mViewModel.setDate(CalendarDay.today(), null);
        //setUpRecyclerView();
    }
}