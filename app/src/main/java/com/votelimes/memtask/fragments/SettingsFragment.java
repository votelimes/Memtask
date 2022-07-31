package com.votelimes.memtask.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.services.CalendarProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends PreferenceFragmentCompat {
    SwitchPreferenceCompat useSync;
    Preference googleSyncPref;

    final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK || result.getResultCode() == Activity.RESULT_CANCELED) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
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
        if(App.getSettings().isAccountSigned(getContext())) {
            googleSyncPref.setSummary(App.getSettings().getAccountEmail(getContext()));
        }
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
                Context context = App.getInstance().getApplicationContext();
                App.getSettings().updateAccount(context);
                if(App.getSettings().isAccountSigned(getContext()) == false) {
                    launcher.launch(CalendarProvider.getSignInIntent(getContext()));
                }
                else{
                    new MaterialAlertDialogBuilder(SettingsFragment.this.getContext())
                            .setTitle("Отвязка аккаунта")
                            .setMessage("Вы действительно хотите отвязать данный аккаунт?")
                            .setPositiveButton("Отвязать", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    App.getSettings().removeAccount(context).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            googleSyncPref.setSummary(App.getSettings().getAccountEmail(getContext()));
                                        }
                                    });
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            App.getSettings().updateAccount(App.getInstance().getApplicationContext());
            googleSyncPref.setSummary(App.getSettings().getAccountEmail(getContext()));

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ERROR: ", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(App.getInstance().getApplicationContext(), "Ошибка авторизации", Toast.LENGTH_SHORT).show();
        }
    }
}