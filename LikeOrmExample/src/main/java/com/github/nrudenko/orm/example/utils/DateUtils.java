package com.github.nrudenko.orm.example.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();

    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_PATTERN = "yyyy-MM-dd";

    private static ArrayList<DateFormat> dateFormatArrayList = new ArrayList<DateFormat>();

    static {
        dateFormatArrayList.add(new SimpleDateFormat(DATABASE_DATE_FORMAT, Locale.getDefault()));
        dateFormatArrayList.add(new SimpleDateFormat(SHORT_DATE_PATTERN, Locale.getDefault()));
    }

    private DateUtils() {
        // not called
    }

    public static Date stringToDate(String stringDate) {
        Date result = null;

        for (int i = 0; i < dateFormatArrayList.size(); i++) {
            DateFormat dateFormat = dateFormatArrayList.get(i);
            try {
                result = dateFormat.parse(stringDate);
                break;
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return result;
    }
}
