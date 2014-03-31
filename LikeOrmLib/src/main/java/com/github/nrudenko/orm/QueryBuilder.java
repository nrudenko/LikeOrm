package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;


public class QueryBuilder<T> {

    private final Uri baseUri;
    protected Uri uri;
    protected String[] projection;
    protected String where;
    protected String[] whereArgs;
    protected String sortOrder;

    public QueryBuilder(Context context, Class<? extends ContentProvider> providerClass) {
        String authority = MetaDataParser.getAuthority(context, providerClass);
        this.baseUri = Uri.parse("content://" + authority);
        this.contentResolver = context.getContentResolver();
    }

    public QueryBuilder<T> table(Class<T> aClass) {
        uri = Uri.withAppendedPath(baseUri, "table/" + aClass.getSimpleName());
        return this;
    }

    public QueryBuilder<T> projection(Object... columns) {
        int length = columns.length;
        this.projection = new String[length];
        for (int i = 0; i < columns.length; i++) {
            Object column = columns[i];
            this.projection[i] = column.toString();
        }
        return this;
    }

    public Cursor query() {
        return contentResolver.query(uri, projection, where, whereArgs, sortOrder);
    }

    public Uri insert(T object) {
        return this.contentResolver.insert(uri, CursorUtil.objectToContentValues(object));
    }

    public int insert(List<T> objects) {
        return contentResolver.bulkInsert(uri, CursorUtil.objectToContentValues(objects));
    }


    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    public Uri getUri() {
        return uri;
    }

    public String[] getProjection() {
        return projection;
    }

    public String getWhere() {
        return where;
    }

    public String[] getWhereArgs() {
        return whereArgs;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    private ContentResolver contentResolver;
}
