package com.udacity.android.makeupapp.utils;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.android.makeupapp.R;

public class ImageLoader {

    public static void loadImage(String pathToImage, ImageView image) {
        Picasso.get()
                .load(pathToImage)
                .error(R.drawable.placeholder_error)
                .into(image);
    }
}
