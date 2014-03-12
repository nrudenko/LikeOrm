package com.github.nrudenko.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseSQLiteOpenHelper extends SQLiteOpenHelper {
    public BaseSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        OrmUri.getInstance(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<Class<? extends OrmModel>> classes = new ArrayList<Class<? extends OrmModel>>();
        appendSchemas(classes);
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < classes.size(); i++) {
            Scheme scheme = new Scheme(classes.get(i));
            appendCreateTableSQL(sql, scheme);
            db.execSQL(sql.toString());
            sql.setLength(0);
        }
    }

    protected abstract String getAuthority();

    protected abstract void appendSchemas(List<Class<? extends OrmModel>> classes);

    protected boolean appendCreateTableSQL(StringBuilder sql, Scheme scheme) {
        sql.append("CREATE TABLE IF NOT EXISTS ").append(scheme.getTableName()).append(" (");
        StringBuilder columnsSql = new StringBuilder(1024);

        ArrayList<Column> columns = scheme.getColumns();
        Iterator<Column> it = columns.iterator();
        while (it.hasNext()) {
            Column column = it.next();
            if (columnsSql.length() > 0) {
                columnsSql.append(",");
            }
            columnsSql.append(column.getColumnName());
            columnsSql
                .append(" ")
                .append(column.getColumnType());
        }

        if (columnsSql.length() > 0) {
            sql.append(columnsSql.toString());
        } else {
            return true;
        }

        String customSql = scheme.getCustomSql();
        if (customSql != null && customSql.length() > 0) {
            sql.append(customSql);
        }
        sql.append(");");
        Log.d("ss", sql.toString());
        return false;
    }

    public static String preparePlaceHolders(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += i > 0 ? ",?" : "?";
        }
        return result;
    }
}
