package com.example.clock.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.clock.adapters.CategoriesListFragmentAdapter;
import com.example.clock.app.App;
import com.example.clock.model.Category;
import com.example.clock.model.Task;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CardsListFragment extends Fragment {

    RecyclerView mRecyclerView;
    CardsListFragmentAdapter mRecyclerViewAdapter;
    MainViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    ConstraintLayout mMainLayoutView;
    Context mContext;

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    //Unpack result
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
                .getApplication());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);

        mRecyclerView = getView().findViewById(R.id.cards_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_cards_constraint);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mViewModel.requestTasksData().observe(getViewLifecycleOwner(), data -> {
            mRecyclerViewAdapter = new CardsListFragmentAdapter(
                    getActivity(),
                    activityLauncher,
                    mViewModel.requestTasksData().getValue(),
                    App.getSettings().getLastCategoryID(), null
            );
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        });

        ExtendedFloatingActionButton addButton = getView()
                .findViewById(R.id.fragment_cards_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageTaskActivity.class);
                //intent.putExtra();


                startActivity(intent);
            }
        });
    }

    final Observer<List<Task>> hoardObserver = new Observer<List<Task>>() {
        @Override
        public void onChanged(@Nullable final List<Task> updatedHoard) {
            mRecyclerViewAdapter = new CardsListFragmentAdapter(
                    getActivity(), activityLauncher,
                    mViewModel.requestTasksData().getValue(),
                    App.getSettings().getLastCategoryID(), null);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    };
}
