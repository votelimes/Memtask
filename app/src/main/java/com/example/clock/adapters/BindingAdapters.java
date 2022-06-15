package com.example.clock.adapters;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.databinding.BindingAdapter;

import com.example.clock.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

public class BindingAdapters {

    @BindingAdapter({"app:textAdv"})
    public static void setTextAdvanced(AutoCompleteTextView view, String text){
        view.setText(text, false);
    }

    @BindingAdapter({"app:textAdv"})
    public static void setCAdvanced(AutoCompleteTextView view, String text){
        view.setText(text, false);
    }

    @BindingAdapter({"app:backgroundColor"})
    public static void setBackgroundColor(View view, int color){
        view.setBackgroundColor(color);
    }

    @BindingAdapter("app:strikeThrough")
    public static void strikeThrough(TextView textView, Boolean strikeThrough) {
        if (strikeThrough) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @BindingAdapter("app:cpb_progress_binding")
    public static void cpbProgressBinding(CircularProgressBar progressBar, float progress) {
        progressBar.setProgress(progress);
    }

    @BindingAdapter("android:typeface")
    public static void setTypeface(EditText v, String style) {
        switch (style) {
            case "bold":
                v.setTypeface(null, Typeface.BOLD);
                break;
            default:
                v.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    @BindingAdapter("android:multiline")
    public static void setMultiline(EditText v, boolean state) {
        if(state){
            v.setSingleLine(false);
            v.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            v.setLines(1);
            v.setMaxLines(7);
            v.setVerticalScrollBarEnabled(true);
            v.setMovementMethod(ScrollingMovementMethod.getInstance());
            v.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        }
        else{
            v.setInputType(InputType.TYPE_NULL);
        }
    }

    @BindingAdapter("android:projectInputType")
    public static void projectInputType(EditText v, boolean state) {
        if(state){
            v.setSingleLine(false);
            v.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            v.setLines(1);
            v.setMaxLines(7);
            v.setVerticalScrollBarEnabled(true);
            v.setMovementMethod(ScrollingMovementMethod.getInstance());
            v.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        }
        else{
            v.setInputType(InputType.TYPE_NULL);
        }
    }

    @BindingAdapter("android:completedExpiredCheckedIcon")
    public static void completedExpiredCheckedIcon(MaterialCardView v, Pair<Boolean, Boolean> completedExpired) {
        Drawable drawable;
        if(completedExpired.first){
            drawable = AppCompatResources.getDrawable(v.getContext(), R.drawable.ic_round_done_all_24);
        }
        else if(completedExpired.second){
            drawable = AppCompatResources.getDrawable(v.getContext(), R.drawable.ic_round_disabled_by_default_24);
        }
        else{
            drawable = AppCompatResources.getDrawable(v.getContext(), R.drawable.ic_round_done_all_24);
        }
        v.setCheckedIcon(drawable);
    }

    @BindingAdapter("android:textImp")
    public static void setText(TextInputEditText v, String text) {
        View parent = (View) v.getParent().getParent();

        if(parent instanceof TextInputLayout){
            TextInputLayout layout = (TextInputLayout) parent;
            v.setText(text);

            layout.setEndIconVisible(true);
            layout.setEndIconActivated(true);
        }
    }

    @BindingAdapter("android:picassoUpdate")
    public static void updateImage(ImageView view, String StringUri){
        if(StringUri != null && StringUri.length() != 0){
            Uri uri = Uri.parse(StringUri);
            View parent = (View)((ViewGroup) view.getParent());

            int h1 = parent.findViewById(R.id.task_name).getHeight();
            int h2 = parent.findViewById(R.id.task_description).getHeight();

            if(h1 == 0 && h2 == 0){
                h1 = 2;
            }

            int medianH = (h1 + h2)/5 + 100;

            try {
                Picasso.get()
                        .load(uri)
                        .resize(0, medianH)
                        .into(view);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @BindingAdapter("android:imageChecked")
    public static void imageViewChecker(ImageView view, Pair<Integer, Integer> data){
        switch (data.first){
            case 0:
                view.setVisibility(View.GONE);
                break;
            case 1:
                view.setVisibility(View.VISIBLE);
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_round_check_circle_outline_24);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, data.second);
                view.setImageDrawable(wrappedDrawable);
                break;
            case 2:
                view.setVisibility(View.VISIBLE);
                Drawable unwrappedDrawable2 = AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_round_remove_circle_outline_24);
                Drawable wrappedDrawable2 = DrawableCompat.wrap(unwrappedDrawable2);
                DrawableCompat.setTint(wrappedDrawable2, data.second);
                view.setImageDrawable(wrappedDrawable2);
                break;
        }
    }
}
