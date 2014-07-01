package com.github.nrudenko.orm.annotation;

import com.github.nrudenko.orm.adapter.BLOBAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeType {
    Class adapter() default BLOBAdapter.class;
}
