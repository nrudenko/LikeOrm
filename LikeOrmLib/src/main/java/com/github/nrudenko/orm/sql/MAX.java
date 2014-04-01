package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public class MAX extends Column {
    public MAX(Column column) {
        super("MAX(" + column.getName() + ")", column.getType());
    }
}
