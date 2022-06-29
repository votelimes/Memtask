package com.example.clock.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.repositories.MemtaskRepositoryBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

class SyncAdapter extends AbstractThreadedSyncAdapter {

    static final String TAG = SyncAdapter.class.getName();

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        MemtaskRepositoryBase repository = new MemtaskRepositoryBase(App.getDatabase(), App.getSilentDatabase());
        repository.synchronizeGCCalendars(App.getInstance().getApplicationContext());
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        Log.i(TAG, "");
    }

    /*@Override
    public void onSecurityException(Account account, Bundle extras, String authority, SyncResult syncResult) {
        super.onSecurityException(account, extras, authority, syncResult);
        Log.i(TAG,"Extras: " + extras);
    }*/
}