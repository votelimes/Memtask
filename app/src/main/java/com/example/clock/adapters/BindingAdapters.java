package com.example.clock.adapters;

import android.util.Log;
import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

public class BindingAdapters {

    @BindingAdapter({"app:textAdv"})
    public static void setTextAdvanced(AutoCompleteTextView view, String text){
        //Log.d("DBG: repeat mode been changed from ", view.getText().toString());
        view.setText(text, false);
        //Log.d("DBG: repeat mode been changed to ", text);
    }
}
