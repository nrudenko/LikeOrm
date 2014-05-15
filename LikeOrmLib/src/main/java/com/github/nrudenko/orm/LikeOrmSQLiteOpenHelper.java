package com.github.nrudenko.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.github.nrudenko.orm.commons.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LikeOrmSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String TAG = LikeOrmSQLiteOpenHelper.class.getSimpleName();

    public LikeOrmSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    protected abstract void appendSchemes(List<Class> classes);

    private List<Class> getSchemesClasses() {
        List<Class> classes = new ArrayList<Class>();
        appendSchemes(classes);
        return classes;
    }

    protected void createTables(SQLiteDatabase db) {
        List<Class> classes = getSchemesClasses();

        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < classes.size(); i++) {
            Scheme scheme = new Scheme(classes.get(i));
            appendCreateTableSQL(sql, scheme);
            db.execSQL(sql.toString());
            sql.setLength(0);
        }
    }

    protected void dropTables(SQLiteDatabase db) {
        List<Class> classes = getSchemesClasses();

        StringBuilder sql = new StringBuilder();
        for (int j = 0; j < classes.size(); j++) {
            sql.setLength(0);
            Class aClass = classes.get(j);
            sql.append("DROP TABLE IF EXISTS ").append(aClass.getSimpleName()).append(";");
            db.execSQL(sql.toString());
        }
    }

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
            columnsSql.append(column.getColumnsSql());
        }

        if (columnsSql.length() > 0) {
            sql.append(columnsSql.toString());
        } else {
            return true;
        }

        String customSql = scheme.getCustomSql();
        if (!TextUtils.isEmpty(customSql)) {
            sql
                    .append(" ")
                    .append(customSql);
        }
        sql.append(");");
        Log.d(TAG, sql.toString());
        return false;
    }

}
