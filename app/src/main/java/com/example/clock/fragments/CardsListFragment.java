package com.example.clock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        //super(R.layout.fragment_cards_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);


        mViewModel.requestTasksData().observe(getViewLifecycleOwner(), data -> {
            mRecyclerViewAdapter = new DefaultFragmentListAdapter(
                    mViewModel.requestTasksData().getValue(),
                    mViewModel.getCurrentCategoryID(), null
            );
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        });
    }
}
