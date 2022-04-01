package com.example.clock.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.app.App;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.Arrays;

public class SettingsFragment extends PreferenceFragmentCompat {
    SwitchPreferenceCompat useSync;
    Preference googleSyncPref;

    final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() != 0) {
                    String acc = result.getData().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if(acc != null && acc.length() > 0) {
                        App.getSettings().setAccount(acc);
                        googleSyncPref.setSummary(acc);
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        try {
            toolbar.findViewById(R.id.action_search).setVisibility(View.GONE);
            toolbar.setTitle("Настройки");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        googleSyncPref = findPreference("memtask_preference_account");
        googleSyncPref.setSummary(App.getSettings().getAccount());

        useSync = findPreference("memtask_preference_use_sync");

        googleSyncPref.setVisible(App.getSettings().getUseSync());


        useSync.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                googleSyncPref.setVisible(((Boolean) newValue).booleanValue());
                return true;
            }
        });

        googleSyncPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AccountManager am = AccountManager.get(getContext()); // "this" references the current Context
                Intent intent = AccountManager.newChooseAccountIntent(null,
                        null,
                        new String[] { "com.google", "com.google.android.legacyimap" },
                        null, null,
                        null, null);

                launcher.launch(intent);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}