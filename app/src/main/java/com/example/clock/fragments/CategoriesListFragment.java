package com.example.clock.fragments;

import android.app.Activity;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clock.R;
import com.example.clock.activities.ManageCategoryActivity;
import com.example.clock.adapters.CategoriesListFragmentAdapter;
import com.example.clock.model.Category;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class CategoriesListFragment extends Fragment {


    RecyclerView mRecyclerView;
    CategoriesListFragmentAdapter mRecyclerViewAdapter;
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

        return inflater.inflate(R.layout.fragment_categories_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                .getActivity()
                .getApplication());

        mContext = getContext();

        mViewModel = new ViewModelProvider(requireActivity(), mFactory).get(MainViewModel.class);

        mRecyclerView = getView().findViewById(R.id.categories_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_categories_constraint);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);


        mRecyclerViewAdapter = new CategoriesListFragmentAdapter(
                getActivity(), activityLauncher,
                mViewModel.requestCategoriesData().getValue());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mViewModel.requestCategoriesData().observe(getViewLifecycleOwner(), hoardObserver);

        ExtendedFloatingActionButton addButton = getView()
                .findViewById(R.id.fragment_categories_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageCategoryActivity.class);
                startActivity(intent);
            }
        });
    }

    final Observer<List<Category>> hoardObserver = new Observer<List<Category>>() {
        @Override
        public void onChanged(@Nullable final List<Category> updatedHoard) {
            mRecyclerViewAdapter = new CategoriesListFragmentAdapter(
                    getActivity(), activityLauncher,
                    mViewModel.requestCategoriesData().getValue());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    };
}