package com.udacity.android.makeupapp.utils;

import android.widget.TextView;

import timber.log.Timber;

import static android.view.View.GONE;
import static com.udacity.android.makeupapp.utils.StringFormatter.capitalize;

public class ViewSetter {

    public static void setTextView(String text, TextView textView) {
        if (text != null && !text.isEmpty()) {
            textView.setText(capitalize(text.trim()));
        } else {
            Timber.d("Text view info not available");
            textView.setVisibility(GONE);
        }
    }
}
