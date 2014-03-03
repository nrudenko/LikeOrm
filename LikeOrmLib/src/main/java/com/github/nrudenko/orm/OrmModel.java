package com.github.nrudenko.orm;

import android.content.ContentValues;
import android.database.Cursor;
import com.github.nrudenko.orm.annotation.*;

public class OrmModel {

    @DbColumn(additional = DbConstants.PRIMARY_AUTOINCREMENT,type = DBType.INT)
    public long _id;

    public ContentValues toContentValues() {
        return ReflectionUtils.objectToContentValues(this);
    }

    public void createFromCursor(Cursor cursor) {
        ReflectionUtils.cursorToObject(cursor, this);
    }
}
