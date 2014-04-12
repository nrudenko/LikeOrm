package com.github.nrudenko.orm;

import android.content.AsyncQueryHandler;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.sql.ASC;
import com.github.nrudenko.orm.sql.SortOrder;
import com.github.nrudenko.orm.sql.TableJoin;

import java.util.ArrayList;
import java.util.List;

import static com.github.nrudenko.orm.CursorUtil.objectToContentValues;


public class QueryBuilder<T> {

    private String table;
    private final Uri baseUri;
    private Uri uri;
    private String[] projection;
    private StringBuilder where = new StringBuilder();
    private ArrayList<String> whereArgs = new ArrayList<String>();
    private StringBuilder orderBy = new StringBuilder();
    private StringBuilder groupBy = new StringBuilder();
    private String limit;

    public QueryBuilder(Context context, Class<? extends ContentProvider> providerClass) {
        String authority = MetaDataParser.getAuthority(context, providerClass);
        this.baseUri = Uri.parse("content://" + authority);
        this.contentResolver = context.getContentResolver();
    }

    public QueryBuilder<T> table(Class<T> aClass, TableJoin... joins) {
        table = aClass.getSimpleName();
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (joins.length > 0) {
            uriBuilder.appendPath("join").appendPath(table);
            for (int i = 0; i < joins.length; i++) {
                TableJoin join = joins[i];
                uriBuilder.appendQueryParameter("join[" + i + "]", join.getSql());
            }
        } else {
            uriBuilder.appendPath("table").appendPath(table);
        }
        uri = uriBuilder.build();
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
        if (where.length() > 0) {
            where.append(" AND ");
        }
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

    public QueryBuilder<T> is(Column column) {
        where
                .append("=")
                .append(column.getName());
        return this;
    }

    public QueryBuilder<T> in(List<String> in) {
        where.append("IN (" + preparePlaceHolders(in.size()) + ")");
        whereArgs.addAll(in);
        return this;
    }

    private void updateWhere(String operation, Object value) {
        if (value instanceof Boolean) {
            String booleanVal = (Boolean) value ? "1" : "0";
            whereArgs.add(booleanVal);
        } else {
            whereArgs.add(value.toString());
        }
        where.append(operation);
    }

    public QueryBuilder<T> orderBy(SortOrder... sortOrders) {
        for (int i = 0; i < sortOrders.length; i++) {
            SortOrder order = sortOrders[i];
            orderBy.append(order.sql);
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
        return contentResolver.query(uri, projection, getWhere(), getWhereArgs(), getOrderBy());
    }

    public void query(OnLoadFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startQuery(0, null, getUri(), getProjection(), getWhere(), getWhereArgs(), getOrderBy());
    }

    public Uri insert(T object) {
        return this.contentResolver.insert(uri, objectToContentValues(object));
    }

    public int insert(List<T> objects) {
        return contentResolver.bulkInsert(uri, objectToContentValues(objects));
    }

    public void insert(T object, OnLoadFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startInsert(0, null, getUri(), objectToContentValues(object));
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
        StringBuilder result = new StringBuilder();
        result.append(where);
        if (groupBy.length() > 0) {
            result
                    .append(" ")
                    .append(groupBy);
        }
        return getStringOrNull(result);
    }

    public String[] getWhereArgs() {
        String[] args = new String[whereArgs.size()];
        whereArgs.toArray(args);
        return args;
    }

    public String getOrderBy() {
        StringBuilder result = new StringBuilder();
        result.append(orderBy);

        if (!TextUtils.isEmpty(limit)) {
            if (TextUtils.isEmpty(orderBy)) {
                result.append(new ASC(Scheme._ID).sql); // default is asc by _id
            }
            result
                    .append(" ")
                    .append(limit);
        }
        return getStringOrNull(result);
    }

    private String getStringOrNull(StringBuilder builder) {
        if (builder != null && builder.length() > 0) {
            return builder.toString();
        }
        return null;
    }

    private ContentResolver contentResolver;

    public String toRawQuery() {
        return SQLiteQueryBuilder.buildQueryString(
                false, table, getProjection(), getWhere(), null, null, getOrderBy(), null);
    }

    public Column asColumn(Column column) {
        StringBuilder raw = new StringBuilder("(");
        raw
                .append(toRawQuery())
                .append(") AS ")
                .append(column.getName());
        return new Column(raw.toString());
    }

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
