package com.github.nrudenko.orm.example.model;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.commons.DbType;

public abstract class BaseModel {

    @DbColumn(type = DbType.INT_PRIMARY)
    protected Long _id;
}
