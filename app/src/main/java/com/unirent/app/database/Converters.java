package com.unirent.app.database;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return "";
        return String.join("\u0001", list);
    }

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split("\u0001", -1)));
    }
}
