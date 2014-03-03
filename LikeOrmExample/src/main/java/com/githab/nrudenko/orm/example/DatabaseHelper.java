package com.githab.nrudenko.orm.example;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.githab.nrudenko.orm.example.model.ExampleModel;
import com.github.nrudenko.orm.BaseSQLiteOpenHelper;
import com.github.nrudenko.orm.Scheme;

import java.util.List;

public class DatabaseHelper extends BaseSQLiteOpenHelper {

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.githab.nrudenko.orm";

    public static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected String getAuthority() {
        return CONTENT_AUTHORITY;
    }

    @Override
    protected void appendSchemas(List<Scheme> schemes) {
        schemes.add(new Scheme(ExampleModel.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
