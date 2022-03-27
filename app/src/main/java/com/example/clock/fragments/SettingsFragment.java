package com.example.clock.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.preference.PreferenceFragmentCompat;

import com.example.clock.R;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.action_search).setVisibility(View.GONE);

        toolbar.setTitle("Настройки");
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }
}