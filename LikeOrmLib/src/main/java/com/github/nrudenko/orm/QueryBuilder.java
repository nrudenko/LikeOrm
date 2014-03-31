package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class QueryBuilder {
    private final Uri baseUri;

    public Uri uri;
    public String[] projection;
    public String where;
    public String[] whereArgs;
    public String sortOrder;

    public QueryBuilder(Context context, Class<? extends ContentProvider> providerClass) {
        String authority = MetaDataParser.getAuthority(context, providerClass);
        baseUri = Uri.parse("content://" + authority);
    }

    public QueryBuilder table(Class aClass) {
        uri = Uri.withAppendedPath(baseUri, "/table/" + aClass.getSimpleName());
        return this;
    }

    public QueryBuilder projection(Object... columns) {
        int length = columns.length;
        this.projection = new String[length];
        for (int i = 0; i < columns.length; i++) {
            Object column = columns[i];
            this.projection[i] = column.toString();
        }
        return this;
    }

    public static QueryBuilder build(Context context, Class<? extends ContentProvider> providerClass) {
        return new QueryBuilder(context, providerClass);
    }

    public Cursor query(ContentResolver contentResolver) {
        return contentResolver.query(uri, projection, where, whereArgs, sortOrder);
    }
    public Uri insert(ContentResolver contentResolver, Object object) {
        return contentResolver.insert(uri, CursorUtil.objectToContentValues(object));
    }
}
