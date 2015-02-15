package com.github.nrudenko.orm.example;

import com.github.nrudenko.orm.LikeOrmContentProvider;
import com.github.nrudenko.orm.LikeOrmSQLiteOpenHelper;

public class SimpleContentProvider extends LikeOrmContentProvider {

    @Override
    public LikeOrmSQLiteOpenHelper getDbHelper() {
        return new DatabaseHelper(getContext());
    }
}
