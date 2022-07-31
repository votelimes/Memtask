package com.votelimes.memtask.services;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.votelimes.memtask.app.App;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;

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