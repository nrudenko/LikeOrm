package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public class DESC extends SortOrder {

    public DESC(Column column) {
        super(column);
    }

    @Override
    protected String getPattern() {
        return "%s DESC";
    }
}