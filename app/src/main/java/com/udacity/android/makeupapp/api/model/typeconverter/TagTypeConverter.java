package com.udacity.android.makeupapp.api.model.typeconverter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TagTypeConverter {

    @TypeConverter
    public static List<String> stringToTagList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String tagListToString(List<String> tags) {
        return new Gson().toJson(tags);
    }
}
