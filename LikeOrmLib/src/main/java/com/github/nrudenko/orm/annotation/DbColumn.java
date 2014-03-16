package com.github.nrudenko.orm.annotation;

import com.github.nrudenko.orm.commons.DBType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbColumn {

    public String name() default "";

    public DBType type() default DBType.TEXT;

    public String additional() default "";
}
