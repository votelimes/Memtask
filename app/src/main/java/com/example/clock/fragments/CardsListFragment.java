package com.example.clock.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.adapters.DefaultFragmentListAdapter;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;

public class CardsListFragment extends Fragment {

    RecyclerView mRecyclerView;
    DefaultFragmentListAdapter mRecyclerViewAdapter;
    MainViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    Context mContext;

    public CardsListFragment(){
        super(R.layout.fragment_cards_list);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment_container_view, CardsListFragment.class, null)
                    .commit();
        }

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                        .getActivity()
                        .getApplication());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);
        mRecyclerView = getView().findViewById(R.id.cards_list);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);

        mViewModel.requestTasksData().observe(getViewLifecycleOwner(), data -> {
            mRecyclerViewAdapter = new DefaultFragmentListAdapter(
                    mViewModel.requestTasksData().getValue(),
                    mViewModel.requestCategoriesData().getValue()
                    );
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        });
    }
}
