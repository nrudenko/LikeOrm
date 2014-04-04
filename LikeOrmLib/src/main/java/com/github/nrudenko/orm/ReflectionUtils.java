package com.github.nrudenko.orm;

import android.text.TextUtils;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.DbSkipField;
import com.github.nrudenko.orm.annotation.VirtualColumn;
import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.commons.DbType;
import com.github.nrudenko.orm.commons.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class ReflectionUtils {

    private static ArrayList<Field> setAllFields(Class cls, ArrayList<Field> fields) {
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        Class superCls = cls.getSuperclass();
        if (superCls != null) {
            setAllFields(superCls, fields);
        }
        return fields;
    }

    public static ArrayList<Field> getClassFields(Class cls) {
        ArrayList<Field> fields = new ArrayList<Field>();
        setAllFields(cls, fields);
        return fields;
    }

    /**
     * Convert java field to Column
     *
     * @param field
     * @return Column or null if convertation failed
     */
    public static Column fieldToColumn(Field field) {
        if (field.isAnnotationPresent(DbSkipField.class)
                || field.isAnnotationPresent(VirtualColumn.class)
                || Modifier.isStatic(field.getModifiers())) {
            return null;
        }
        DbColumn dbColumn = field.getAnnotation(DbColumn.class);

        String columnName = null;
        String customAdditional = null;
        DbType dbType = null;

        if (dbColumn != null) {
            columnName = dbColumn.name();
            dbType = dbColumn.type();
            customAdditional = dbColumn.additional();
        }

        if (TextUtils.isEmpty(columnName)) {
            columnName = field.getName();
        }

        if (dbType == null || dbType.equals(DbType.NO_TYPE)) {
            FieldType fieldType = FieldType.byTypeClass(field.getType());
            if (fieldType != null) {
                dbType = fieldType.getDbType();
            }
        }

        Column column = null;
        if (!TextUtils.isEmpty(columnName) || dbType != null) {
            column = new Column(columnName, dbType);
            if (!TextUtils.isEmpty(customAdditional)) {
                column.setCustomAdditional(customAdditional);
            }
        }
        return column;
    }

}
