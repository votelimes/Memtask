package com.votelimes.memtask.storageutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class Settings {

    public final boolean TESTING = true;

    // 0 Calendar, 1 Category list, 2 Tasks list, 3 Statistic, 4 Settings.
    private Pair<Long, String> mCurrentWindow;
    private Pair<Boolean, String> mSetupState;
    private Pair<String, String> mLastCategoryID;
    private Pair<String, String> mLastCategoryName;
    private Pair<String, String> mUseDarkTheme;
    private Pair<Boolean, String> mUseRandomThemes;
    private Pair<String, String> mCalendarMode;
    private Pair<Boolean, String> mUseSync;
    private Pair<String, String> mAccountEmail;


    private GoogleSignInAccount mSignedAccount;
    private final SharedPreferences mSharedPref;


    public Settings(@NonNull Context context) {

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        updateData(context);

        updateAccount(context);
    }

    public void updateData(Context context){

        String mCurrentWindowPrefTag = "current_window";
        mCurrentWindow = new Pair<Long, String>
                (mSharedPref.getLong(mCurrentWindowPrefTag, 2), mCurrentWindowPrefTag);

        String mSetupStateTag = "Time";
        mSetupState = new Pair<Boolean, String>
                (mSharedPref.getBoolean(mSetupStateTag, false), mSetupStateTag);

        String mLastCategoryIDTag = "last_category_id";
        mLastCategoryID = new Pair<String, String>
                (mSharedPref.getString(mLastCategoryIDTag, ""), mLastCategoryIDTag);

        String mLastCategoryNameTag = "last_category_name";
        mLastCategoryName = new Pair<String, String>
                (mSharedPref.getString(mLastCategoryNameTag, "Категория"), mLastCategoryNameTag);

        //Settings fragment prefs
        String mUseRandomThemesTag = "use_random_themes";
        mUseRandomThemes = new Pair<Boolean, String>
                (mSharedPref.getBoolean(mUseRandomThemesTag, true), mUseRandomThemesTag);

        String mCalendarModeTag = "calendar_mode";
        mCalendarMode = new Pair<String, String>
                (mSharedPref.getString(mCalendarModeTag, App.getInstance().getString(R.string.preference_calendar_mode_value_default)), mCalendarModeTag);

        String mUseDarkThemeTag = context.getResources().getString(R.string.preference_theme_key);
        mUseDarkTheme = new Pair<String, String>
                (mSharedPref.getString(mUseDarkThemeTag,
                        context
                        .getResources()
                        .getStringArray(R.array.preference_light_theme_value)[0]), mUseDarkThemeTag);

        String mUseSyncTag = "memtask_preference_use_sync";
        mUseSync = new Pair<Boolean, String>
                (mSharedPref.getBoolean(mUseSyncTag, false), mUseSyncTag);

        String mAccountTag = "memtask_preference_account";
        mAccountEmail = new Pair<String, String>
        (mSharedPref.getString(mAccountTag, ""), mAccountTag);
    }

    public SharedPreferences getSharedPref(){
        return this.mSharedPref;
    }

    public long getCurrentWindow(){
        return this.mCurrentWindow.first.longValue();
    }

    public void setCurrentWindow(long id){
        SharedPreferences.Editor editor = mSharedPref.edit();
        mCurrentWindow = new Pair<Long, String>
                (id, mCurrentWindow.second);


        editor.putLong(mCurrentWindow.second, id);

        editor.commit();
    }

    public void setLastCategory(String id, String name){


        SharedPreferences.Editor editor = mSharedPref.edit();

        mLastCategoryID = new Pair<String, String>
                (id, mLastCategoryID.second);

        editor.putString(mLastCategoryID.second, id);

        mLastCategoryName = new Pair<String, String>
                (name, mLastCategoryName.second);

        editor.putString(mLastCategoryName.second, name);

        editor.commit();
    }

    public Pair<String, String> getLastCategory(){
        return new Pair<String , String>(mLastCategoryID.first, mLastCategoryName.first);
    }

    public boolean getSetupState(){
        return this.mSetupState.first;
    }

    public void setSetupState(boolean state){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(mSetupState.second, state);

        editor.commit();
    }

    public String getUseDarkTheme(){
        return this.mUseDarkTheme.first;
    }

    public boolean getGenerateRandomThemes(){
        return this.mUseRandomThemes.first;
    }

    public String getCalendarMode(){
        return this.mCalendarMode.first;
    }

    public String getAccountEmail(Context context){
        updateAccount(context);
        if(mSignedAccount != null) {
            return this.mSignedAccount.getEmail();
        }
        else{
            return "";
        }
    }

    public void setAccount(String acc){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(mAccountEmail.second, acc);
        editor.commit();
    }

    public boolean getUseSync(){
        return mUseSync.first;
    }

    public void setUseSync(boolean val){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(mUseSync.second, val);
        editor.commit();
    }

    public boolean isAccountSigned(Context context){
        updateAccount(context);
        return mSignedAccount != null;
    }

    public void updateAccount(Context context){
        mSignedAccount = GoogleSignIn.getLastSignedInAccount(context);
        if(mSignedAccount == null && (mAccountEmail.first != null || mAccountEmail.first.length() != 0)){
            setAccount("");
        }
    }

    public Task<Void> removeAccount(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        GoogleSignInClient gsi = GoogleSignIn.getClient(context, gso);
        return gsi.signOut();
    }

    public GoogleSignInAccount getAccount(){
        return mSignedAccount;
    }
}
