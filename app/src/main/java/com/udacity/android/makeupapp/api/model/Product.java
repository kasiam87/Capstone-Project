package com.udacity.android.makeupapp.api.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.udacity.android.makeupapp.api.model.typeconverter.ColorTypeConverter;
import com.udacity.android.makeupapp.api.model.typeconverter.TagTypeConverter;

@Entity(tableName = "favorites")
public class Product {

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
    @TypeConverters(TagTypeConverter.class)  //or @Embedded ??
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
    @TypeConverters(ColorTypeConverter.class) //or @Embedded ??
    public List<ProductColor> productColors;

}