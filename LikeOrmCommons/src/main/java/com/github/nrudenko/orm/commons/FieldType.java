package com.github.nrudenko.orm.commons;


import java.util.Date;

public enum FieldType {
    INTEGER(DbType.INT, Integer.class, int.class),
    STRING(DbType.TEXT, String.class),
    BOOLEAN(DbType.INT, Boolean.class, boolean.class),
    LONG(DbType.NUMERIC, Long.class, long.class),
    BYTE(DbType.INT, Byte.class, byte.class),
    SHORT(DbType.INT, Short.class, short.class),
    FLOAT(DbType.REAL, Float.class, float.class),
    DOUBLE(DbType.REAL, Double.class, double.class),
    BLOB(DbType.BLOB, Byte[].class, byte[].class),
    DATE(DbType.NUMERIC, Date.class),
    ENUM(DbType.TEXT, Enum.class),
    SERIALIZED(DbType.SERIALIZED, new Class[0]); // no automatic assignation

    private final Class[] cls;
    private final DbType dbType;

    private FieldType(DbType dbType, Class... cls) {
        this.dbType = dbType;
        this.cls = cls;
    }

    public Class[] getTypeClass() {
        return cls;
    }

    public DbType getDbType() {
        return dbType;
    }

    public static FieldType byDbType(DbType type) {
        for (FieldType fieldType : FieldType.values()) {
            if (fieldType.getDbType().equals(type)) {
                return fieldType;
            }
        }
        return null;
    }

    public static FieldType byTypeClass(Class cls) {
        return findBy(classPredicate, cls);
    }

    public static FieldType byTypeName(String clsName) {
        return findBy(classNamePredicate, clsName);
    }

    private static FieldType findBy(FieldTypeClassPredicate predicate, Object condition) {
        if (condition != null) {
            for (FieldType b : FieldType.values()) {
                Class[] typeClass = b.getTypeClass();
                for (int i = 0; i < typeClass.length; i++) {
                    Class typeClazz = typeClass[i];
                    if (predicate.isConditionEquals(typeClazz, condition)) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    static FieldTypeClassPredicate<String> classNamePredicate = new FieldTypeClassPredicate<String>() {
        @Override
        public boolean isConditionEquals(Class cls, String condition) {
            if (condition.equals(cls.getSimpleName())) {
                return true;
            }
            return false;
        }
    };

    static FieldTypeClassPredicate<Class> classPredicate = new FieldTypeClassPredicate<Class>() {
        @Override
        public boolean isConditionEquals(Class cls, Class condition) {
            if (cls.isAssignableFrom(condition)) {
                return true;
            }
            return false;
        }
    };

    interface FieldTypeClassPredicate<T> {
        boolean isConditionEquals(Class cls, T condition);
    }
}
