package com.github.nrudenko.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.text.TextUtils;
import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.DbSkipField;
import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.commons.DBType;
import com.github.nrudenko.orm.commons.FieldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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

    public static ContentValues objectToContentValues(Object model) {
        ArrayList<Field> classFields = getClassFields(model.getClass());
        ContentValues contentValues = new ContentValues();
        for (Field field : classFields) {
            String fieldName = field.getName();
            try {
                if (field.isAnnotationPresent(DbSkipField.class)) {
                    continue;
                }
                FieldType key = FieldType.byTypeClass(field.getType());
                field.setAccessible(true);
                Object value = field.get(model);
                if (key != null && value != null) {
                    switch (key) {
                        case INTEGER:
                            contentValues.put(fieldName, (Integer) value);
                            break;
                        case STRING:
                            contentValues.put(fieldName, (String) value);
                            break;
                        case BYTE:
                            contentValues.put(fieldName, (Byte) value);
                            break;
                        case SHORT:
                            contentValues.put(fieldName, (Short) value);
                            break;
                        case LONG:
                            contentValues.put(fieldName, (Long) value);
                            break;
                        case FLOAT:
                            contentValues.put(fieldName, (Float) value);
                            break;
                        case DOUBLE:
                            contentValues.put(fieldName, (Double) value);
                            break;
                        case BOOLEAN:
                            contentValues.put(fieldName, (Boolean) value);
                            break;
                        case BLOB:
                            contentValues.put(fieldName, (byte[]) value);
                            break;
                        case DATE:
                            contentValues.put(fieldName, ((Date) value).getTime());
                            break;
                        default:
                            break;
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return contentValues;
    }

    public static void cursorToObject(Cursor cursor, Object model) {
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        ArrayList<Field> fields = ReflectionUtils.getClassFields(model.getClass());

        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                if (field.isAnnotationPresent(DbSkipField.class)) {
                    continue;
                }
                Class<?> type = field.getType();
                FieldType fieldType = FieldType.byTypeClass(type);
                field.setAccessible(true);
                if (fieldType != null) {
                    switch (fieldType) {
                        case INTEGER:
                            field.set(model, contentValues.getAsInteger(fieldName));
                            break;
                        case STRING:
                            field.set(model, contentValues.getAsString(fieldName));
                            break;
                        case BYTE:
                            field.set(model, contentValues.getAsByte(fieldName));
                            break;
                        case SHORT:
                            field.set(model, contentValues.getAsShort(fieldName));
                            break;
                        case LONG:
                            field.set(model, contentValues.getAsLong(fieldName));
                            break;
                        case FLOAT:
                            field.set(model, contentValues.getAsFloat(fieldName));
                            break;
                        case DOUBLE:
                            field.set(model, contentValues.getAsDouble(fieldName));
                            break;
                        case BOOLEAN:
                            field.set(model, contentValues.getAsBoolean(fieldName));
                            break;
                        case BLOB:
                            field.set(model, contentValues.getAsByteArray(fieldName));
                            break;
                        case DATE:
                            Long timeMs = contentValues.getAsLong(fieldName);
                            if (timeMs != null) {
                                field.set(model, new Date(timeMs));
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Column fieldToColumn(Field field) {
        if (!field.isAnnotationPresent(DbSkipField.class)) {
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
        return null;
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
                    dbType = DBType.TEXT;
                    break;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return dbType;
    }
}
