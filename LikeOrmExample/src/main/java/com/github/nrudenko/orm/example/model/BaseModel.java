package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.commons.DBType;

public abstract class BaseModel {

    @DbColumn(type = DBType.INT_PRIMARY)
    protected long _id;
}
