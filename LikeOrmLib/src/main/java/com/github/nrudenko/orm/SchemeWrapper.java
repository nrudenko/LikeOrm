package com.github.nrudenko.orm;

import android.util.Log;

import com.github.nrudenko.orm.annotation.Table;
import com.github.nrudenko.orm.commons.BaseScheme;
import com.github.nrudenko.orm.commons.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class SchemeWrapper {
    private static final String TAG = SchemeWrapper.class.getSimpleName();

    private final BaseScheme scheme;

    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String PROJECTION = "PROJECTION";
    public static final String CUSTOM_SQL = "CUSTOM_SQL";
    public static final String MODEL_CLASS = "MODEL_CLASS";

    private HashMap<String, Object> fields = new HashMap<String, Object>();

    {
        fields.put(TABLE_NAME, null);
        fields.put(PROJECTION, null);
        fields.put(CUSTOM_SQL, null);
        fields.put(MODEL_CLASS, null);
    }

    public SchemeWrapper(Class<? extends BaseScheme> schemeClass) {
        String classSimpleName = schemeClass.getSimpleName();
        try {
            scheme = schemeClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Can't wrap " + classSimpleName + " class:", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Can't wrap " + classSimpleName + " class:", e);
        }

        Iterator<String> iterator = fields.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                fields.put(key, scheme.getClass().getField(key));
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "Field " + key + " in scheme " + classSimpleName);
            }
        }

        Class modelClass = getModelClass();
        Table table = (Table) modelClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException(modelClass.getSimpleName() + " class must use Table annotation");
        }
    }

    private Object getValue(String key) {
        Object result = null;
        try {
            result = ((Field) fields.get(key)).get(scheme);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(key + " in " + scheme.getClass().getSimpleName() + " not accessible:", e);
        } finally {
            if (result == null) {
                throw new IllegalArgumentException(key + " in " + scheme.getClass().getSimpleName() + " can't be null");
            }
        }

        return result;
    }

    public String getTableName() {
        return (String) getValue(TABLE_NAME);
    }

    public ArrayList<Column> getColumns() {
        Column[] projection = (Column[]) getValue(PROJECTION);
        ArrayList<Column> result = new ArrayList<Column>(Arrays.asList(projection));
        return result;
    }

    public String getCustomSql() {
        return (String) getValue(CUSTOM_SQL);
    }

    public Class getModelClass() {
        return (Class) getValue(MODEL_CLASS);
    }
}
