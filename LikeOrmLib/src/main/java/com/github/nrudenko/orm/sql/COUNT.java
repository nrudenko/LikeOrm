package com.github.nrudenko.orm.sql;

import com.github.nrudenko.orm.commons.Column;

public class COUNT extends Column {
    public COUNT() {
        super("count(*)");
    }

    public COUNT(Column column) {
        super("count(" + column.getName() + ")");
    }
}
