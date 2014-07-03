package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import java.util.List;

public abstract class LikeOrmContentProvider extends ContentProvider {

    private static final String TAG = LikeOrmContentProvider.class.getSimpleName();

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
        // join/mainTable?table2="LEFT JOIN table2 ON column1=column2"
        matcher.addURI(authority, "join/*", JOIN);
        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = getDbHelper().getReadableDatabase();
        String table = getTable(uri);
        String groupBy = LikeOrmUriHelper.getGroupBy(uri);
        String having = null;

        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case SINGLE_TABLE:
            case JOIN:
                cursor = db.query(table, projection, selection, selectionArgs, groupBy, having, sortOrder);
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
        int deleteResult = -1;
        try {
            deleteResult = db.delete(table, selection, selectionArgs);
            if (deleteResult > 0) {
                notifyUri(uri);
            }
        } catch (SQLiteException e) {
            Log.w(TAG, e.toString());
        } finally {
            return deleteResult;
        }
    }

    private void notifyUri(Uri uri) {
        if (getContext() != null && LikeOrmUriHelper.shouldNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    private String getTable(Uri uri) {
        List<String> segments = uri.getPathSegments();
        StringBuilder table = new StringBuilder(segments.get(segments.size() - 1));
        switch (uriMatcher.match(uri)) {
            case SINGLE_TABLE:
                // in all cases
                break;
            case JOIN:
                List<String> joins = LikeOrmUriHelper.getJoins(uri);
                for (String join : joins) {
                    table.append(" ");
                    table.append(join);
                }
                break;
            default:
                throw new RuntimeException("Can't find table from uri: " + uri);
        }
        return table.toString();
    }

}
