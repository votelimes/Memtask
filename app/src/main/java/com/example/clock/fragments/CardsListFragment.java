package com.example.clock.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.adapters.CardsListFragmentAdapter;
import com.example.clock.app.App;
import com.example.clock.model.ProjectData;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Tuple3;
import com.example.clock.viewmodels.CategoryActivitiesViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class CardsListFragment extends Fragment {

    RecyclerView mRecyclerView;
    CardsListFragmentAdapter mRecyclerViewAdapter;
    CategoryActivitiesViewModel mViewModel;
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
                else if(result.getResultCode() == 30){
                    Toast.makeText(getActivity(), "Проект создан", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Изменения отменены", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cards_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                .getActivity()
                .getApplication(), App.getDatabase(), App.getSilentDatabase());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(CategoryActivitiesViewModel.class);

        mViewModel.loadData();

        mRecyclerView = getView().findViewById(R.id.cards_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_cards_constraint);

        toolbar = getActivity().findViewById(R.id.toolbar);

        toolbar.findViewById(R.id.action_search).setVisibility(View.VISIBLE);

        toolbar.setTitle(App.getSettings().getLastCategory().second);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mViewModel.intermediate.observe(getViewLifecycleOwner(), hoardObserver);

        FloatingActionButton fabTask = getView().findViewById(R.id.fragment_cards_fab_task);
        FloatingActionButton fabProject = getView().findViewById(R.id.fragment_cards_fab_proj);
        FloatingActionMenu fabMenu = getView().findViewById(R.id.fragment_cards_fabl);


        fabTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                taskIntent.putExtra("mode", "TaskCreating");
                taskIntent.putExtra("category", App.getSettings().getLastCategory().first);*/
                fabMenu.close(true);
                mViewModel.addTaskChild();
                mRecyclerViewAdapter.notifyItemInserted(0);
                mRecyclerViewAdapter.scrollTo(0);
                mRecyclerViewAdapter.setAddedOutside(true);


                //activityLauncher.launch(taskIntent);
            }
        });
        fabProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent projectIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                projectIntent.putExtra("mode", "ProjectCreating");
                projectIntent.putExtra("category", App.getSettings().getLastCategory().first);
                fabMenu.close(true);
                activityLauncher.launch(projectIntent);
            }
        });

            }

    final Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> hoardObserver = new Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>> updatedHoard) {

            mViewModel.init();
            mRecyclerViewAdapter = new CardsListFragmentAdapter(
                    activityLauncher, mViewModel, mMainLayoutView, mLayoutManager);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    };
}
