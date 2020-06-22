package com.udacity.android.makeupapp.api.model.typeconverter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.udacity.android.makeupapp.api.model.ProductColor;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ColorTypeConverter {

    @TypeConverter
    public static List<ProductColor> stringToColorsList(String value) {
        if (value == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<ProductColor>>() {}.getType();

        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String colorsListToString(List<ProductColor> colors) {
        return new Gson().toJson(colors);
    }
}
