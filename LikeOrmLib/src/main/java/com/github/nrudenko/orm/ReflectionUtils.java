package com.github.nrudenko.orm;

import android.text.TextUtils;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.DbSkipField;
import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.commons.DBType;
import com.github.nrudenko.orm.commons.FieldType;

import java.lang.reflect.Field;
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

    public static Column fieldToColumn(Field field) {
        if (field.isAnnotationPresent(DbSkipField.class)) {
            return null;
        }

        DbColumn dbColumn = field.getAnnotation(DbColumn.class);

        String columnName = null;
        String type = null;
        String customAdditional = null;

        if (dbColumn != null) {
            columnName = dbColumn.name();
            type = dbColumn.type().getName();
            customAdditional = dbColumn.additional();
        }

        if (TextUtils.isEmpty(columnName)) {
            columnName = field.getName();
        }

        if (TextUtils.isEmpty(type)) {
            FieldType key = FieldType.byTypeClass(field.getType());
            if (key != null) {
                DBType dbType = getDbType(key);
                type = dbType.getName();
            }
        }

        if (!TextUtils.isEmpty(customAdditional)) {
            type = TextUtils.concat(type, " ", customAdditional).toString();
        }

        return new Column(columnName, type);
    }

    private static DBType getDbType(FieldType key) {
        DBType dbType = null;
        try {
            switch (key) {
                case INTEGER:
                    dbType = DBType.INT;
                    break;
                case STRING:
                    dbType = DBType.TEXT;
                    break;
                case BYTE:
                    dbType = DBType.BLOB;
                    break;
                case SHORT:
                    dbType = DBType.NUMERIC;
                    break;
                case LONG:
                    dbType = DBType.NUMERIC;
                    break;
                case FLOAT:
                    dbType = DBType.REAL;
                    break;
                case DOUBLE:
                    dbType = DBType.REAL;
                    break;
                case BOOLEAN:
                    dbType = DBType.INT;
                    break;
                case DATE:
                    dbType = DBType.NUMERIC;
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return dbType;
    }
}
