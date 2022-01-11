package com.example.clock.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.adapters.CalendarFragmentAdapter;
import com.example.clock.app.App;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Tuple2;
import com.example.clock.viewmodels.CalendarViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class CalendarFragment extends Fragment {
    RecyclerView mRecyclerView;
    CalendarFragmentAdapter mRecyclerViewAdapter;
    CalendarViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    ConstraintLayout mMainLayoutView;
    Context mContext;
    MaterialToolbar toolbar;

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 20) {
                    Toast.makeText(getActivity(), "Задача добавлена", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Изменения отменены", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                .getActivity()
                .getApplication(), getViewLifecycleOwner(),App.getDatabase(), App.getSilentDatabase());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(CalendarViewModel.class);

        mRecyclerView = getView().findViewById(R.id.calendar_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_calendar_constraint);

        toolbar = getActivity().findViewById(R.id.topAppBar);

        toolbar.setTitle("Календарь активностей");

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mViewModel.intermediate.observe(getViewLifecycleOwner(), hoardObserver);

        FloatingActionButton fabTask = getView().findViewById(R.id.fragment_calendar_fab_task);


        fabTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                taskIntent.putExtra("mode", "Task");
                activityLauncher.launch(taskIntent);
            }
        });
    }

    final Observer<Tuple2<List<Task>, List<Theme>>> hoardObserver = new Observer<Tuple2<List<Task>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple2<List<Task>, List<Theme>> updatedHoard) {
            mViewModel.init(mViewModel.getSelectedDateStart(), null);
            mRecyclerViewAdapter = new CalendarFragmentAdapter(
                    activityLauncher, mViewModel);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    };
}