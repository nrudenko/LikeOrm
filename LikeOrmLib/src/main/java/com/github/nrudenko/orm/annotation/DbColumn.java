package com.github.nrudenko.orm.annotation;

import com.github.nrudenko.orm.DBType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbColumn {

    public String columnName() default "";

    public DBType columnType() default DBType.TEXT;

    public String additional() default "";
}
