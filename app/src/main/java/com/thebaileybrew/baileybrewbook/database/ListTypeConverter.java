package com.thebaileybrew.baileybrewbook.database;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thebaileybrew.baileybrewbook.database.models.Step;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class ListTypeConverter {

    @TypeConverter
    public static List<Step> stringToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Step>>() {}.getType();
        List<Step> steps = gson.fromJson(json, type);
        return steps;
    }

    @TypeConverter
    public static String listToString(List<Step> steps) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Step>>() {}.getType();
        String json = gson.toJson(steps, type);
        return json;
    }
}
