package com.github.nrudenko.orm.commons;


import java.util.Date;

public enum FieldType {
    INTEGER(DBType.INT, Integer.class, int.class),
    STRING(DBType.TEXT, String.class),
    BOOLEAN(DBType.INT, boolean.class),
    LONG(DBType.NUMERIC, long.class),
    BYTE(DBType.INT, byte.class),
    SHORT(DBType.INT, short.class),
    FLOAT(DBType.REAL, float.class),
    DOUBLE(DBType.REAL, double.class),
    BLOB(DBType.BLOB, byte[].class),
    DATE(DBType.NUMERIC, Date.class);

    private final Class[] cls;
    private final DBType dbType;

    private FieldType(DBType dbType, Class... cls) {
        this.dbType = dbType;
        this.cls = cls;
    }

    public Class[] getTypeClass() {
        return cls;
    }

    public DBType getDbType() {
        return dbType;
    }

    public String getDbTypeReference() {
        return DBType.class.getSimpleName() + "." + dbType.getName();
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

    static FieldTypeClassPredicate classNamePredicate = new FieldTypeClassPredicate() {
        @Override
        public boolean isConditionEquals(Class cls, Object condition) {
            if (condition.equals(cls.getSimpleName())) {
                return true;
            }
            return false;
        }
    };

    static FieldTypeClassPredicate classPredicate = new FieldTypeClassPredicate() {
        @Override
        public boolean isConditionEquals(Class cls, Object condition) {
            if (condition.equals(cls)) {
                return true;
            }
            return false;
        }
    };

    interface FieldTypeClassPredicate {
        boolean isConditionEquals(Class cls, Object condition);
    }
}
