package com.udacity.android.makeupapp.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ProductColor implements Parcelable {

    @SerializedName("hex_value")
    public String hexValue;


    protected ProductColor(Parcel in) {
        hexValue = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hexValue);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductColor> CREATOR = new Parcelable.Creator<ProductColor>() {
        @Override
        public ProductColor createFromParcel(Parcel in) {
            return new ProductColor(in);
        }

        @Override
        public ProductColor[] newArray(int size) {
            return new ProductColor[size];
        }
    };
}