package com.github.nrudenko.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.github.nrudenko.orm.commons.BaseScheme;
import com.github.nrudenko.orm.commons.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LikeOrmSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String TAG = LikeOrmSQLiteOpenHelper.class.getSimpleName();

    public static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    public LikeOrmSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    protected abstract void appendSchemes(List<Class<? extends BaseScheme>> schemes);

    private List<Class<? extends BaseScheme>> getSchemesClasses() {
        List<Class<? extends BaseScheme>> schemes = new ArrayList<Class<? extends BaseScheme>>();
        appendSchemes(schemes);
        return schemes;
    }

    protected void createTables(SQLiteDatabase db) {
        List<Class<? extends BaseScheme>> schemesClasses = getSchemesClasses();

        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < schemesClasses.size(); i++) {
            SchemeWrapper schemeWrapper = new SchemeWrapper(schemesClasses.get(i));
            String tableSql = generateTableSQL(schemeWrapper);
            Log.i(TAG, tableSql);
            sql.append(tableSql);
        }
        db.execSQL(sql.toString());
    }

    protected void dropTables(SQLiteDatabase db) {
        List<Class<? extends BaseScheme>> schemesClasses = getSchemesClasses();

        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < schemesClasses.size(); i++) {
            sql.setLength(0);
            SchemeWrapper schemeWrapper = new SchemeWrapper(schemesClasses.get(i));
            sql.append(DROP_TABLE_IF_EXISTS).append(schemeWrapper.getTableName()).append(";");
            db.execSQL(sql.toString());
        }
    }

    protected String generateTableSQL(SchemeWrapper schemeWrapper) {
        StringBuilder columnsSql = new StringBuilder();

        ArrayList<Column> columns = schemeWrapper.getColumns();
        Iterator<Column> it = columns.iterator();

        while (it.hasNext()) {
            Column column = it.next();
            columnsSql.append(column.getColumnsSql());
            columnsSql.append(",");
        }
        StringBuilder tableSql = new StringBuilder();
        if (columnsSql.length() > 0) {

            columnsSql.setLength(columnsSql.length() - 1);

            tableSql.append(CREATE_TABLE_IF_NOT_EXISTS)
                    .append(schemeWrapper.getTableName())
                    .append(" (")
                    .append(columnsSql)
                    .append(")");
            String customSql = schemeWrapper.getCustomSql();
            if (!TextUtils.isEmpty(customSql)) {
                tableSql.append(" ")
                        .append(customSql);
            }
            tableSql.append(";");
        }
        return tableSql.toString();
    }

}
