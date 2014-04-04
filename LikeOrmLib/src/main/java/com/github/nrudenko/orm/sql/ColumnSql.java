package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public abstract class ColumnSql {
    public final String sql;

    public ColumnSql(Column column) {
        sql = String.format(getPattern(), column.getName());
    }

    protected abstract String getPattern();
}
