package com.github.nrudenko.orm;


import java.util.Date;

public enum FieldType {
    INTEGER(Integer.class, int.class),
    STRING(String.class),
    BOOLEAN(Boolean.class, boolean.class),
    LONG(Long.class, long.class),
    BYTE(Byte.class, byte.class),
    SHORT(Short.class, short.class),
    FLOAT(Float.class, float.class),
    DOUBLE(Double.class, double.class),
    BLOB(byte[].class),
    DATE(Date.class);

    private final Class[] cls;

    private FieldType(Class... cls) {
        this.cls = cls;
    }


    public Class[] getTypeClass() {
        return cls;
    }

    public static FieldType byType(Class cls) {
        if (cls != null) {
            for (FieldType b : FieldType.values()) {
                Class[] typeClass = b.getTypeClass();
                for (int i = 0; i < typeClass.length; i++) {
                    Class typeClas = typeClass[i];
                    if (cls.equals(typeClas)) {
                        return b;
                    }
                }

            }
        }
        return null;
    }
}
