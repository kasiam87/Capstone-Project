package com.udacity.android.makeupapp.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.udacity.android.makeupapp.api.model.typeconverter.ColorTypeConverter;
import com.udacity.android.makeupapp.api.model.typeconverter.TagTypeConverter;

@Entity(tableName = "favorites")
public class Product implements Serializable, Parcelable {

    @PrimaryKey
    public Integer id;

    public String brand;

    public String name;

    public String price;

    @SerializedName("price_sign")
    public String priceSign;

    public String currency;

    @SerializedName("image_link")
    public String imageLink;

    @SerializedName("product_link")
    public String productLink;

    @SerializedName("website_link")
    public String websiteLink;

    public String description;

    public Float rating;

    public String category;

    @SerializedName("product_type")
    public String productType;

    @SerializedName("tag_list")
    @TypeConverters(TagTypeConverter.class)
    public List<String> tagList;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    @SerializedName("product_api_url")
    public String productApiUrl;

    @SerializedName("api_featured_image")
    public String apiFeaturedImage;

    @SerializedName("product_colors")
    @TypeConverters(ColorTypeConverter.class)
    public List<ProductColor> productColors;

    public Product(){

    }

    protected Product(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        brand = in.readString();
        name = in.readString();
        price = in.readString();
        priceSign = in.readString();
        currency = in.readString();
        imageLink = in.readString();
        productLink = in.readString();
        websiteLink = in.readString();
        description = in.readString();
        rating = in.readByte() == 0x00 ? null : in.readFloat();
        category = in.readString();
        productType = in.readString();
        if (in.readByte() == 0x01) {
            tagList = new ArrayList<>();
            in.readList(tagList, String.class.getClassLoader());
        } else {
            tagList = null;
        }
        createdAt = in.readString();
        updatedAt = in.readString();
        productApiUrl = in.readString();
        apiFeaturedImage = in.readString();
        if (in.readByte() == 0x01) {
            productColors = new ArrayList<>();
            in.readList(productColors, ProductColor.class.getClassLoader());
        } else {
            productColors = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(brand);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(priceSign);
        dest.writeString(currency);
        dest.writeString(imageLink);
        dest.writeString(productLink);
        dest.writeString(websiteLink);
        dest.writeString(description);
        if (rating == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(rating);
        }
        dest.writeString(category);
        dest.writeString(productType);
        if (tagList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tagList);
        }
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(productApiUrl);
        dest.writeString(apiFeaturedImage);
        if (productColors == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(productColors);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}