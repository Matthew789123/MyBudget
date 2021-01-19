package com.example.mybudget.Database;

import android.net.Uri;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converter {

    @TypeConverter
    public static Date fromDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toDate(Date date) {
        return date == null ? null : date.getTime();
    }
}

