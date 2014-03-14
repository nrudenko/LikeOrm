package com.github.nrudenko.orm;

import android.content.ContentValues;
import android.database.Cursor;
import com.github.nrudenko.orm.annotation.*;

public class OrmModel {

    @DbColumn(columnType = DBType.INT_PRIMARY)
    public long _id;

    public ContentValues toContentValues() {
        return ReflectionUtils.objectToContentValues(this);
    }

    public void createFromCursor(Cursor cursor) {
        ReflectionUtils.cursorToObject(cursor, this);
    }
}
