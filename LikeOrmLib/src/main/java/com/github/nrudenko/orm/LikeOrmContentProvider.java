package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;

public abstract class LikeOrmContentProvider extends ContentProvider {

    public static final int ENTITY = 100;

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

        matcher.addURI(authority, "table/*", ENTITY);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = getDbHelper().getReadableDatabase();
        String table = getTable(uri);

        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case ENTITY:
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
        switch (uriMatcher.match(uri)) {
            case ENTITY:
                List<String> segments = uri.getPathSegments();
                tableName = segments.get(segments.size() - 1);
                break;
            default:
                throw new RuntimeException("Can't find table from uri: " + uri);
        }
        return tableName;
    }

}
