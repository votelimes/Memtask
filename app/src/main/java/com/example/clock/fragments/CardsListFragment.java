package com.example.clock.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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

public class CardsListFragment extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView mRecyclerView;
    CardsListFragmentAdapter mRecyclerViewAdapter;
    CategoryActivitiesViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    ConstraintLayout mMainLayoutView;
    Context mContext;
    MaterialToolbar toolbar;
    SearchView searchView;


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

        mViewModel.intermediate.observe(getViewLifecycleOwner(), onCreateObserver);

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
                mRecyclerViewAdapter.setAddedOutside(0);


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

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        ItemTouchHelper.SimpleCallback touchHelperCallbackRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CardsListFragmentAdapter.ProjectViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
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
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CardsListFragmentAdapter.ProjectViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                if(viewHolder instanceof CardsListFragmentAdapter.TaskViewHolder){
                    CardsListFragmentAdapter.TaskViewHolder vh = (CardsListFragmentAdapter.TaskViewHolder) viewHolder;
                    taskIntent.putExtra("ID", vh.getBinding().getData().getTask().getTaskId());
                    taskIntent.putExtra("mode", "TaskEditing");
                }
                else if(viewHolder instanceof CardsListFragmentAdapter.ProjectViewHolder){
                    CardsListFragmentAdapter.ProjectViewHolder vh = (CardsListFragmentAdapter.ProjectViewHolder) viewHolder;
                    taskIntent.putExtra("ID", vh.getBinding().getData().getProject().getProjectId());
                    taskIntent.putExtra("mode", "ProjectEditing");
                }
                activityLauncher.launch(taskIntent);
                CardsListFragmentAdapter adapter = (CardsListFragmentAdapter) viewHolder.getBindingAdapter();
                adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
            }
        };
        itemTouchHelper = new ItemTouchHelper(touchHelperCallbackLeft);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }

    final Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> onCreateObserver = new Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>> updatedHoard) {

            mViewModel.init();
            mRecyclerViewAdapter = new CardsListFragmentAdapter(
                    activityLauncher, mViewModel, mMainLayoutView, mLayoutManager);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    };

    final Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> onUpdateObserver = new Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>> updatedHoard) {
            mViewModel.init();
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mViewModel.updateData(newText).observe(this, onUpdateObserver);
        return false;
    }
}
