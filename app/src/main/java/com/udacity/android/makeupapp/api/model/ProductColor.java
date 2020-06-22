package com.udacity.android.makeupapp.api.model;

import com.google.gson.annotations.SerializedName;

public class ProductColor {

    @SerializedName("hex_value")
    public String hexValue;

    @SerializedName("colour_name")
    public String colourName;

}