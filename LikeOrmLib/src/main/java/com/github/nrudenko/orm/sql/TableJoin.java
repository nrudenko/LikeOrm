package com.github.nrudenko.orm.sql;

import android.text.TextUtils;

import com.github.nrudenko.orm.commons.Column;

public class TableJoin {
    private JoinType type;
    Class cls;
    Column columnLeft;
    Column columnRight;

    public static enum JoinType {
        LEFT, RIGHT
    }

    public TableJoin(Class cls, Column columnLeft, Column columnRight) {
        this(JoinType.LEFT, cls, columnLeft, columnRight);
    }

    public TableJoin(JoinType type, Class cls, Column columnLeft, Column columnRight) {
        this.type = type;
        this.cls = cls;
        this.columnLeft = columnLeft;
        this.columnRight = columnRight;
    }

    public String getSql() {
        return TextUtils.concat(
                        type.toString(),
                        " JOIN ",
                        cls.getSimpleName(),
                        " ON ",
                        columnLeft.getName(),
                        "=",
                        columnRight.getName()
                ).toString();
    }

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public Column getColumnLeft() {
        return columnLeft;
    }

    public void setColumnLeft(Column columnLeft) {
        this.columnLeft = columnLeft;
    }

    public Column getColumnRight() {
        return columnRight;
    }

    public void setColumnRight(Column columnRight) {
        this.columnRight = columnRight;
    }
}
