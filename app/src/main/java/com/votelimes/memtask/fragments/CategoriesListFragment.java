package com.votelimes.memtask.fragments;

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

import com.votelimes.memtask.R;
import com.votelimes.memtask.activities.ManageCategoryActivity;
import com.votelimes.memtask.adapters.CategoriesListFragmentAdapter;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.Category;
import com.votelimes.memtask.viewmodels.MainViewModel;
import com.votelimes.memtask.viewmodels.ViewModelFactoryBase;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;

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
                if (result.getResultCode() == 10) {
                    Toast.makeText(getActivity(), "Категория сохранена", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Изменения отменены", Toast.LENGTH_SHORT).show();
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
                .getApplication(), App.getDatabase(), App.getSilentDatabase());

        mContext = getContext();

        mViewModel = new ViewModelProvider(requireActivity(), mFactory).get(MainViewModel.class);


        mRecyclerView = getView().findViewById(R.id.categories_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_categories_constraint);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mViewModel.requestCategoriesData().observe(getViewLifecycleOwner(), hoardObserver);

        FloatingActionButton addButton = getView()
                .findViewById(R.id.fragment_categories_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageCategoryActivity.class);
                startActivity(intent);
            }
        });

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.action_search).setVisibility(View.VISIBLE);
        toolbar.setTitle("Категории");
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