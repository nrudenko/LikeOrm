package com.github.nrudenko.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.github.nrudenko.orm.annotation.DbSkipField;
import com.github.nrudenko.orm.commons.FieldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

public class CursorUtil {

    private CursorUtil() {
    }

    public static <T> T cursorToObject(Cursor cursor, T model) {
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
                        case ENUM:
                            String value = contentValues.getAsString(fieldName);
                            try {
                                Field enumField = field.getType().getField(value);
                                field.set(model, enumField.get(model));
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                                //TODO handle errors
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                //TODO handle errors
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                //TODO handle errors
            }
        }
        return model;
    }

    public static ContentValues objectToContentValues(Object model) {
        ArrayList<Field> classFields = ReflectionUtils.getClassFields(model.getClass());
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
                        case ENUM:
                            contentValues.put(fieldName, value.toString());
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
}
