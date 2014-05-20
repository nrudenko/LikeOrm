package com.github.nrudenko.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.github.nrudenko.orm.annotation.DbColumn;
import com.github.nrudenko.orm.annotation.DbSkipField;
import com.github.nrudenko.orm.annotation.VirtualColumn;
import com.github.nrudenko.orm.commons.DbType;
import com.github.nrudenko.orm.commons.FieldType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CursorUtil {

    private CursorUtil() {
    }

    public static <T> T cursorToObject(Cursor cursor, Class<T> modelClass) {
        if (cursor.getCount() == 0) {
            return null;
        }
        return buildModel(modelClass, cursor);
    }

    public static <T> List<T> cursorToList(Cursor cursor, Class<T> modelClass) {
        List<T> items = new ArrayList<T>();
        while (cursor.moveToNext()) {
            final T model = buildModel(modelClass, cursor);
            items.add(model);
        }
        return items;
    }

    private static <T> T buildModel(Class<T> modelClass, Cursor cursor) {
        T model;
        try {
            model = modelClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<Field> fields = ReflectionUtils.getClassFields(modelClass);

        for (Field field : fields) {
            String fieldName;
            if (field.isAnnotationPresent(DbColumn.class) && !TextUtils.isEmpty(field.getAnnotation(DbColumn.class).name())) {
                fieldName = field.getAnnotation(DbColumn.class).name();
            } else {
                fieldName = field.getName();
            }
            try {
                if (field.isAnnotationPresent(DbSkipField.class)) {
                    continue;
                }
                Class<?> type = field.getType();
                FieldType fieldType;
                if (field.isAnnotationPresent(DbColumn.class) && field.getAnnotation(DbColumn.class).type() != DbType.NO_TYPE) {
                    fieldType = FieldType.byDbType(field.getAnnotation(DbColumn.class).type());
                } else if (field.isAnnotationPresent(VirtualColumn.class) && field.getAnnotation(VirtualColumn.class).type() != DbType.NO_TYPE) {
                    fieldType = FieldType.byDbType(field.getAnnotation(VirtualColumn.class).type());
                } else {
                    fieldType = FieldType.byTypeClass(type);
                }
                field.setAccessible(true);
                final int columnIndex = cursor.getColumnIndex(fieldName);
                if (fieldType != null && columnIndex != -1) {
                    switch (fieldType) {
                        case INTEGER:
                            field.set(model, cursor.getInt(columnIndex));
                            break;
                        case STRING:
                            field.set(model, cursor.getString(columnIndex));
                            break;
                        case BYTE:
                            field.set(model, ((byte) cursor.getInt(columnIndex)));
                            break;
                        case SHORT:
                            field.set(model, cursor.getShort(columnIndex));
                            break;
                        case LONG:
                            field.set(model, cursor.getLong(columnIndex));
                            break;
                        case FLOAT:
                            field.set(model, cursor.getFloat(columnIndex));
                            break;
                        case DOUBLE:
                            field.set(model, cursor.getDouble(columnIndex));
                            break;
                        case BOOLEAN:
                            field.set(model, cursor.getInt(columnIndex) == 1);
                            break;
                        case BLOB:
                            field.set(model, cursor.getBlob(columnIndex));
                            break;
                        case SERIALIZED:
                            final byte[] byteArray = cursor.getBlob(columnIndex);
                            if (byteArray == null) {
                                continue;
                            }
                            if (Serializable.class.isAssignableFrom(field.getType())) {
                                ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
                                ObjectInputStream ois = null;
                                try {
                                    ois = new ObjectInputStream(bis);
                                    Object o = ois.readObject();
                                    field.set(model, o);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        bis.close();
                                        if (ois != null)
                                            ois.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (BuildConfig.DEBUG)
                                    Log.w(CursorUtil.class.getSimpleName(), "Can't read SERIALIZED field '" + fieldName + "' – not Serializable");
                            }
                            break;
                        case DATE:
                            Long timeMs = cursor.getLong(columnIndex);
                            if (timeMs != null) {
                                field.set(model, new Date(timeMs));
                            }
                            break;
                        case ENUM:
                            String enumName = cursor.getString(columnIndex);
                            if (!TextUtils.isEmpty(enumName)) {
                                try {
                                    Field enumField = field.getType().getField(enumName);
                                    field.set(model, enumField.get(model));
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                    //TODO handle errors
                                }
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

    public static ContentValues[] objectToContentValues(List items) {
        ContentValues[] contentValues = new ContentValues[items.size()];
        // TODO sync for items?
        for (int i = 0; i < items.size(); i++) {
            Object o = items.get(i);
            contentValues[i] = objectToContentValues(o);
        }
        return contentValues;
    }

    public static ContentValues objectToContentValues(Object model) {
        ArrayList<Field> classFields = ReflectionUtils.getClassFields(model.getClass());
        ContentValues contentValues = new ContentValues();
        for (Field field : classFields) {
            String fieldName;
            if (field.isAnnotationPresent(DbColumn.class) && !TextUtils.isEmpty(field.getAnnotation(DbColumn.class).name())) {
                fieldName = field.getAnnotation(DbColumn.class).name();
            } else {
                fieldName = field.getName();
            }
            try {
                if (field.isAnnotationPresent(DbSkipField.class) || field.isAnnotationPresent(VirtualColumn.class)) {
                    continue;
                }
                FieldType key;
                if (field.isAnnotationPresent(DbColumn.class) && field.getAnnotation(DbColumn.class).type() != DbType.NO_TYPE) {
                    key = FieldType.byDbType(field.getAnnotation(DbColumn.class).type());
                } else {
                    key = FieldType.byTypeClass(field.getType());
                }
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
                        case SERIALIZED:
                            if (Serializable.class.isAssignableFrom(field.getType())) {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ObjectOutputStream oos = null;
                                try {
                                    oos = new ObjectOutputStream(bos);
                                    oos.writeObject(value);
                                    oos.flush();
                                    bos.close();
                                    contentValues.put(fieldName, bos.toByteArray());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        bos.close();
                                        if (oos != null)
                                            oos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (BuildConfig.DEBUG)
                                    Log.w(CursorUtil.class.getSimpleName(), "Can't save SERIALIZED field '" + fieldName + "' – not Serializable");
                            }
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
