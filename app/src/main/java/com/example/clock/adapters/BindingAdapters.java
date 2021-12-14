package com.example.clock.adapters;

import static com.example.clock.app.App.*;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import com.example.clock.R;
import com.example.clock.app.App;
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

    @BindingAdapter({"app:completeness"})
    public static void completeness(View view, boolean state){

        if(state == false){
            view.setBackground(getInstance().getDrawable(R.drawable.progress_check_48));
        }
        else{
            view.setBackground(getInstance().getDrawable(R.drawable.outline_check_circle_black_48));
        }
    }

    public static ColorDrawable convertInttoColorDrawable(int color) {
        return new ColorDrawable(color);
    }

}
