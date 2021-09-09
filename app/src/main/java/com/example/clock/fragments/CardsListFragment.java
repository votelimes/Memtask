package com.example.clock.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.clock.R;

public class CardsListFragment extends Fragment {
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

    }
}
