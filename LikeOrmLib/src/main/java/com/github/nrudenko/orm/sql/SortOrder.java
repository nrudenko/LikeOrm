package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public abstract class SortOrder extends ColumnSql{
    public SortOrder(Column column) {
        super(column);
    }
}
