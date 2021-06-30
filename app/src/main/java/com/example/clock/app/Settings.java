package com.example.clock.app;

import androidx.core.content.ContextCompat;
import android.content.Context;

import com.example.clock.R;

import java.util.HashMap;
import java.util.Map;

public class Settings
{
    public boolean isTaskFieldShows = true;
    public boolean isIdeaFieldShows = true;
    public boolean isNoteFieldShows = true;
    //public boolean isTaskFieldShows = true;

    public boolean is24HTimeUses = false;

    private Map<String, Integer> color;

    public Settings(Context context){
        color = new HashMap<>();

        color.put("mainTheme1", ContextCompat.getColor(context, // Theme colors INIT
                R.color.mainTheme1));
        color.put("mainTheme2", ContextCompat.getColor(context,
                R.color.mainTheme2));
        color.put("mainTheme3", ContextCompat.getColor(context,
                R.color.mainTheme3));
        color.put("mainTheme4", ContextCompat.getColor(context,
                R.color.mainTheme4));
        color.put("mainTheme5", ContextCompat.getColor(context,
                R.color.mainTheme5));
    }
    public int getColor(String colorTag){
        return color.get(colorTag);
    }
}
