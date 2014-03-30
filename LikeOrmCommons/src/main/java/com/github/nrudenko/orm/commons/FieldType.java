package com.github.nrudenko.orm.commons;


import java.util.Date;

public enum FieldType {
    INTEGER(DbType.INT, Integer.class, int.class),
    STRING(DbType.TEXT, String.class),
    BOOLEAN(DbType.INT, boolean.class),
    LONG(DbType.NUMERIC, long.class),
    BYTE(DbType.INT, byte.class),
    SHORT(DbType.INT, short.class),
    FLOAT(DbType.REAL, float.class),
    DOUBLE(DbType.REAL, double.class),
    BLOB(DbType.BLOB, byte[].class),
    DATE(DbType.NUMERIC, Date.class),
    ENUM(DbType.TEXT, Enum.class);

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

    public String getDbTypeReference() {
        return DbType.class.getSimpleName() + "." + dbType.toString();
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
