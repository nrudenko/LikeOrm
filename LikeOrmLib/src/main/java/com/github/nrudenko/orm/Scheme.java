package com.github.nrudenko.orm;

import com.github.nrudenko.orm.commons.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Scheme {
    Class<? extends OrmModel> modelClass;

    public Scheme(Class<? extends OrmModel> modelClass) {
        this.modelClass = modelClass;
    }

    public String getTableName() {
        return modelClass.getSimpleName();
    }

    public ArrayList<Column> getColumns() {
        ArrayList<Column> result = new ArrayList<Column>();
        ArrayList<Field> classFields = ReflectionUtils.getClassFields(modelClass);
        for (int i = 0; i < classFields.size(); i++) {
            Field field = classFields.get(i);
            Column column = ReflectionUtils.fieldToColumn(field);
            if (column != null) {
                result.add(column);
            }
        }
        return result;
    }

    public String getCustomSql() {
        return null;
    }
}
