package com.github.nrudenko.orm;

import android.content.AsyncQueryHandler;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.sql.SortOrder;

import java.util.ArrayList;
import java.util.List;

import static com.github.nrudenko.orm.CursorUtil.objectToContentValues;


public class QueryBuilder<T> {

    private final Uri baseUri;
    private Uri uri;
    private String[] projection;
    private StringBuilder where;
    private List<String> whereArgs = new ArrayList<String>();
    private StringBuilder sortOrder;
    private StringBuilder groupBy;
    private String limit;

    public QueryBuilder(Context context, Class<? extends ContentProvider> providerClass) {
        String authority = MetaDataParser.getAuthority(context, providerClass);
        this.baseUri = Uri.parse("content://" + authority);
        this.contentResolver = context.getContentResolver();
    }

    public QueryBuilder<T> table(Class<T> aClass) {
        uri = Uri.withAppendedPath(baseUri, "table/" + aClass.getSimpleName());
        return this;
    }

    public QueryBuilder<T> projection(Column... columns) {
        int length = columns.length;
        this.projection = new String[length];
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            this.projection[i] = column.getName();
        }
        return this;
    }

    public QueryBuilder<T> where(Column column) {
        where.append(column.getName());
        return this;
    }

    public QueryBuilder<T> less(Object value) {
        updateWhere("<?", value);
        return this;
    }

    public QueryBuilder<T> more(Object value) {
        updateWhere(">?", value);
        return this;
    }

    public QueryBuilder<T> is(Object value) {
        updateWhere("=?", value);
        return this;
    }

    public QueryBuilder<T> in(List<String> in) {
        where.append("IN (" + preparePlaceHolders(in.size()) + ")");
        whereArgs.addAll(in);
        return this;
    }

    private void updateWhere(String operation, Object value) {
        whereArgs.add(value.toString());
        where.append(operation);
    }

    public QueryBuilder<T> sortOrder(SortOrder... sortOrders) {
        for (int i = 0; i < sortOrders.length; i++) {
            SortOrder order = sortOrders[i];
            sortOrder.append(order.sql);
        }
        return this;
    }

    public QueryBuilder<T> groupBy(Column... columns) {
        groupBy = new StringBuilder("GROUP BY ");
        columnsNameJoin(columns);
        return this;
    }

    public QueryBuilder limit(int count) {
        limit = "LIMIT " + count;
        return this;
    }

    private void columnsNameJoin(Column[] columns) {
        String comma = ",";
        boolean firstTime = true;
        for (Column column : columns) {
            if (firstTime) {
                firstTime = false;
            } else {
                groupBy.append(comma);
            }
            groupBy.append(column.getName());
        }
    }

    public static String preparePlaceHolders(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += i > 0 ? ",?" : "?";
        }
        return result;
    }

    public Cursor query() {
        return contentResolver.query(uri, projection, getWhere(), getWhereArgs(), getSortOrder());
    }

    public void query(OnLoadFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startQuery(0, null, getUri(), getProjection(), getWhere(), getWhereArgs(), getSortOrder());
    }

    public Uri insert(T object) {
        return this.contentResolver.insert(uri, objectToContentValues(object));
    }

    public int insert(List<T> objects) {
        return contentResolver.bulkInsert(uri, objectToContentValues(objects));
    }

    public int update(T object) {
        return contentResolver.update(getUri(), objectToContentValues(object), getWhere(), getWhereArgs());
    }

    public int delete() {
        return contentResolver.delete(getUri(), getWhere(), getWhereArgs());
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
        if (groupBy != null) {
            where.append(" ").append(groupBy.toString());
        }
        return where.toString();
    }

    public String[] getWhereArgs() {
        return (String[]) whereArgs.toArray();
    }

    public String getSortOrder() {
        if (!TextUtils.isEmpty(limit)) {
            if (sortOrder != null) {
                sortOrder.append(limit);
            } else {
                sortOrder = new StringBuilder(limit);
            }
        }
        return sortOrder.toString();
    }

    private ContentResolver contentResolver;

    class QueryLoader extends AsyncQueryHandler {

        private final OnLoadFinishedListener onLoadFinishedListener;

        public QueryLoader(ContentResolver cr, OnLoadFinishedListener onLoadFinishedListener) {
            super(cr);
            this.onLoadFinishedListener = onLoadFinishedListener;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            onLoadFinishedListener.onLoadFinished(cursor);
        }
    }

    public static interface OnLoadFinishedListener {
        public void onLoadFinished(Cursor cursor);
    }

}
