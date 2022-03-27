package com.example.clock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.clock.R;
import com.example.clock.activities.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;


public class AboutFragment extends Fragment {

    MaterialToolbar toolbar;
    String backStackTitle = "";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        toolbar = (MaterialToolbar) getActivity().findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).drawerLayout.close();

        toolbar.findViewById(R.id.action_search).setVisibility(View.GONE);
        backStackTitle = (String) toolbar.getTitle();
        toolbar.setTitle("О приложении");

        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
        toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setNavigationIcon(null);
                getActivity().getSupportFragmentManager().popBackStack();
                ((MainActivity) getActivity()).setupNav();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolbar.getMenu().findItem(R.id.action_search).setVisible(true);
        toolbar.setTitle(backStackTitle);
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}