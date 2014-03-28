package com.github.nrudenko.orm;

import android.content.Context;
import android.net.Uri;

public class OrmUri {
    private static OrmUri instance;
    private static Uri BASE_URI;

    private OrmUri(Context context) {
        String authority = OrmMetaData.getAuthority(context);
        BASE_URI = Uri.parse("content://" + authority);
    }

    public static OrmUri getInstance(Context context) {
        if (instance == null) {
            instance = new OrmUri(context);
        }
        return instance;
    }

    public static Uri get(Class<?> modelClass) {
        Uri contentUri = BASE_URI.buildUpon()
                .appendPath("model")
                .appendPath(new Scheme(modelClass).getTableName())
                .build();
        return contentUri;
    }
}
