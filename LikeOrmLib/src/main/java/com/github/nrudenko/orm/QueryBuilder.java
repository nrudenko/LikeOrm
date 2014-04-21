package com.github.nrudenko.orm;

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

    private final LikeOrmUriHelper.Builder uriBuilder;
    private String table;
    //    private Uri uri;
    private String[] projection;
    private StringBuilder where = new StringBuilder();
    private ArrayList<String> whereArgs = new ArrayList<String>();
    private StringBuilder orderBy = new StringBuilder();
    private String limit;

    public QueryBuilder(Context context) {
        uriBuilder = new LikeOrmUriHelper.Builder(context);
        this.contentResolver = context.getContentResolver();
    }

    public QueryBuilder(Context context, Class<? extends ContentProvider> providerClass) {
        uriBuilder = new LikeOrmUriHelper.Builder(context, providerClass);
        this.contentResolver = context.getContentResolver();
    }

    public QueryBuilder<T> table(Class<T> aClass, TableJoin... joins) {
        table = aClass.getSimpleName();
        uriBuilder.addTable(table, joins);
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

    public QueryBuilder<T> isNot(Object value) {
        updateWhere("<>?", value);
        return this;
    }


    public QueryBuilder<T> in(List<String> in) {
        where.append(" IN (" + preparePlaceHolders(in.size()) + ")");
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
        String[] orders = new String[sortOrders.length];
        for (int i = 0; i < sortOrders.length; i++) {
            SortOrder order = sortOrders[i];
            orders[i] = order.sql;
        }
        orderBy.append(TextUtils.join(",", orders).toString());
        return this;
    }

    public QueryBuilder<T> groupBy(Column... columns) {
        String comma = ",";
        StringBuilder groupBy = new StringBuilder();
        boolean firstTime = true;
        for (Column column : columns) {
            if (firstTime) {
                firstTime = false;
            } else {
                groupBy.append(comma);
            }
            groupBy.append(column.getName());
        }
        uriBuilder.addGroupBy(groupBy.toString());
        return this;
    }

    public QueryBuilder limit(int count) {
        limit = "LIMIT " + count;
        return this;
    }

    public static String preparePlaceHolders(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += i > 0 ? ",?" : "?";
        }
        return result;
    }

    public Cursor query() {
        return contentResolver.query(getUri(), projection, getWhere(), getWhereArgs(), getOrderBy());
    }

    public void query(OnFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startQuery(0, null, getUri(), getProjection(), getWhere(), getWhereArgs(), getOrderBy());
    }

    public Uri insert(T object) {
        return this.contentResolver.insert(getUri(), objectToContentValues(object));
    }

    public int insert(List<T> objects) {
        return contentResolver.bulkInsert(getUri(), objectToContentValues(objects));
    }

    public void insert(T object, OnFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startInsert(0, null, getUri(), objectToContentValues(object));
    }

    public void insert(List<T> objects, OnFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startBulkInsert(0, null, getUri(), objectToContentValues(objects));
    }

    public int update(T object) {
        return contentResolver.update(getUri(), objectToContentValues(object), getWhere(), getWhereArgs());
    }

    public void update(T object, OnFinishedListener loadFinishedListener) {
        new QueryLoader(contentResolver, loadFinishedListener).startUpdate
                (0, null, getUri(), objectToContentValues(object), getWhere(), getWhereArgs());
    }

    public int delete() {
        return contentResolver.delete(getUri(), getWhere(), getWhereArgs());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters / Setters
    ///////////////////////////////////////////////////////////////////////////

    public Uri getUri() {
        return uriBuilder.build();
    }

    public String[] getProjection() {
        return projection;
    }

    public String getWhere() {
        StringBuilder result = new StringBuilder();
        result.append(where);
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
        String bindedWhere = getWhere();
        String[] whereArgsForBind = getWhereArgs();
        for (int i = 0; i < whereArgsForBind.length; i++) {
            String arg = whereArgsForBind[i];
            bindedWhere = bindedWhere.replaceFirst("\\?", arg);
        }
        return SQLiteQueryBuilder.buildQueryString(
                false, table, getProjection(), bindedWhere, null, null, getOrderBy(), null);
    }

    public Column asColumn(Column column) {
        StringBuilder raw = new StringBuilder("(");
        raw
                .append(toRawQuery())
                .append(") AS ")
                .append(column.getName());
        return new Column(raw.toString());
    }

    public QueryBuilder<T> withoutNotifying() {
        uriBuilder.withoutNotify();
        return this;
    }

    class QueryLoader extends AsyncQueryHandler {

        private final OnFinishedListener onFinishedListener;

        public QueryLoader(ContentResolver cr, OnFinishedListener onFinishedListener) {
            super(cr);
            this.onFinishedListener = onFinishedListener;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (onFinishedListener != null) {
                onFinishedListener.onQueryFinished(cursor);
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            if (onFinishedListener != null) {
                if (onFinishedListener instanceof SimpleOnFinishedListener)
                    ((SimpleOnFinishedListener) onFinishedListener).onInsertFinished();
            }
        }
    }

    public static interface OnFinishedListener {
        public void onQueryFinished(Cursor cursor);
    }

    public static class SimpleOnFinishedListener implements OnFinishedListener {
        public void onQueryFinished(Cursor cursor) {
        }

        public void onInsertFinished() {
        }
    }

}
