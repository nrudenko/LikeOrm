package com.github.nrudenko.orm;

import com.github.nrudenko.orm.annotation.Table;
import com.github.nrudenko.orm.commons.Column;
import com.github.nrudenko.orm.commons.DbType;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Scheme {
    final Table table;
    final Class<?> modelClass;

    public Scheme(Class modelClass) throws ClassCastException, IllegalArgumentException {
        this.modelClass = modelClass;
        this.table = (Table) modelClass.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("Scheme candidate class must use Table annotation");
        }
    }

    public String getTableName() {
        final String name = modelClass.getAnnotation(Table.class).name();
        return name.isEmpty() ? modelClass.getSimpleName() : name;
    }

    public ArrayList<Column> getColumns() {
        ArrayList<Column> result = new ArrayList<Column>();
        result.add(_ID);
        ArrayList<Field> classFields = ReflectionUtils.getClassFields(modelClass);
        for (int i = 0; i < classFields.size(); i++) {
            Field field = classFields.get(i);
            Column column = ReflectionUtils.fieldToColumn(field);
            if (column != null && column.isCorrect()) {
                result.add(column);
            }
        }
        return result;
    }

    public String getCustomSql() {
        return modelClass.getAnnotation(Table.class).customSql();
    }

    public static final Column _ID = new Column("_id", DbType.INT, "PRIMARY KEY AUTOINCREMENT");
}
