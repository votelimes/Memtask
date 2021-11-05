package com.example.clock.adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import com.google.android.material.button.MaterialButton;

public class BindingAdapters {

    @BindingAdapter({"app:textAdv"})
    public static void setTextAdvanced(AutoCompleteTextView view, String text){
        //Log.d("DBG: repeat mode been changed from ", view.getText().toString());
        view.setText(text, false);
        //Log.d("DBG: repeat mode been changed to ", text);
    }

    @BindingAdapter({"app:textAdv"})
    public static void setCAdvanced(AutoCompleteTextView view, String text){
        //Log.d("DBG: repeat mode been changed from ", view.getText().toString());
        view.setText(text, false);
        //Log.d("DBG: repeat mode been changed to ", text);
    }

    @BindingAdapter({"app:backgroundColor"})
    public static void setBackgroundColor(View view, int color){
        int testColor1 = Color.parseColor("#F15946");
        int testColor2 = 0xFFF15946;


        view.setBackgroundColor(color);
    }

    public static ColorDrawable convertInttoColorDrawable(int color) {
        return new ColorDrawable(color);
    }

}
