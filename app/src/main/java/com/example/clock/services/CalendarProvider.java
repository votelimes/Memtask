package com.example.clock.services;

import android.content.Context;
import android.content.Intent;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.clock.R;
import com.example.clock.app.App;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;

import java.io.IOException;
import java.util.Arrays;

public class CalendarProvider {
    private static final String SCOPES[] =
            {"https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events",
            "https://www.googleapis.com/auth/calendar.addons.execute"};

    private final MutableLiveData<CalendarList> calendarListData;
    private final MutableLiveData<CalendarList> eventListData;

    Calendar service;

    public CalendarProvider(Context context){
        calendarListData = new MutableLiveData<>();
        eventListData = new MutableLiveData<>();

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleAccountCredential credential = getCredential(context);

        service = new Calendar
                .Builder(httpTransport, jsonFactory, credential)
                .build();
    }

    public static Intent getSignInIntent(Context context){
        Scope scopeCalendar = new Scope(SCOPES[0]);
        Scope scopeEvents = new Scope(SCOPES[1]);
        Scope scopeAddons = new Scope(SCOPES[2]);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestScopes(scopeCalendar, scopeEvents, scopeAddons)
                .requestIdToken(context.getResources().getString(R.string.oauth_client_id))
                .build();
        GoogleSignInClient gsi = GoogleSignIn.getClient(App.getInstance().getApplicationContext(), gso);
        return gsi.getSignInIntent();
    }

    public static GoogleAccountCredential getCredential(Context context){
        GoogleAccountCredential accountCredential;

        accountCredential = GoogleAccountCredential
                .usingOAuth2(context, Arrays.asList(SCOPES));
        accountCredential.setSelectedAccountName(App.getSettings().getAccount().getEmail());

        return accountCredential;
    }

    public Pair<LiveData<CalendarList>, Calendar> getCalendars(){
        final CalendarList[] list = {null};
        Calendar.CalendarList.List calendars = null;
        try{
            calendars = service
                    .calendarList()
                    .list();
            Calendar.CalendarList.List finalCalendars = calendars;
            (new Thread(){
                public void run(){
                    try {
                        list[0] = finalCalendars.execute();
                        calendarListData.postValue(list[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new Pair<>(calendarListData, service);
    }
}
