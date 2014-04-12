package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class LikeOrmContentProvider extends ContentProvider {

    public static final int SINGLE_TABLE = 100;
    private static final int JOIN = 101;

    private UriMatcher uriMatcher;
    private String authority;

    @Override
    public boolean onCreate() {
        authority = MetaDataParser.getAuthority(getContext(), getClass());
        uriMatcher = buildUriMatcher();
        return true;
    }

    protected UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(authority, "table/*", SINGLE_TABLE);
        // join/table1?joins[1]="LEFT JOIN table2 on column1=column2"
        matcher.addURI(authority, "join/*", JOIN);
        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = getDbHelper().getReadableDatabase();
        String table = getTable(uri);

        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case SINGLE_TABLE:
            case JOIN:
                cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }

        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    public abstract LikeOrmSQLiteOpenHelper getDbHelper();

    @Override
    public String getType(Uri uri) {
        //TODO need to implement
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = getDbHelper().getWritableDatabase();
        String table = getTable(uri);
        long id = db.insert(table, null, contentValues);
        notifyUri(uri);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = getDbHelper().getWritableDatabase();
        String table = getTable(uri);
        int count = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                db.insert(table, null, value);
                count++;
            }
            db.setTransactionSuccessful();
            notifyUri(uri);
        } finally {
            db.endTransaction();
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = getDbHelper().getWritableDatabase();
        String table = getTable(uri);
        int result = db.update(table, contentValues, selection, selectionArgs);
        notifyUri(uri);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = getDbHelper().getWritableDatabase();
        String table = getTable(uri);
        int deleteResult = db.delete(table, selection, selectionArgs);
        if (deleteResult > 0) {
            notifyUri(uri);
        }
        return deleteResult;
    }

    private void notifyUri(Uri uri) {
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    private String getTable(Uri uri) {
        String tableName;
        List<String> segments = uri.getPathSegments();
        switch (uriMatcher.match(uri)) {
            case SINGLE_TABLE:
                tableName = segments.get(segments.size() - 1);
                break;
            case JOIN:
                StringBuilder table = new StringBuilder(segments.get(segments.size() - 1));
                Set<String> queryParameterNames = uri.getQueryParameterNames();
                Iterator<String> iterator = queryParameterNames.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    table.append(" ");
                    table.append(uri.getQueryParameter(key));
                }
                tableName = table.toString();
                break;
            default:
                throw new RuntimeException("Can't find table from uri: " + uri);
        }
        return tableName;
    }

}
