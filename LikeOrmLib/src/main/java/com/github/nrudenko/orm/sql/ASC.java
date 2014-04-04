package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public class ASC extends SortOrder {

    public ASC(Column column) {
        super(column);
    }

    @Override
    protected String getPattern() {
        return  "%s ASC";
    }
}
