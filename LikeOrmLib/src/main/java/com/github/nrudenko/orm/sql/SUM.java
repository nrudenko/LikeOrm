package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public class SUM extends Column {
    public SUM(Column column) {
        super("SUM(" + column.getName() + ")", column.getType());
    }

}
