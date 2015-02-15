package com.github.nrudenko.orm.sql;

import android.text.TextUtils;

import com.github.nrudenko.orm.commons.Column;

public class MAX extends Column {
    public MAX(Column column) {
        super(TextUtils.concat("MAX(", column.getName(), ")").toString(), column.getType());
    }
}
