package com.lizhi.builder;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SqlBuilder {
    protected static final Map<Class, String> simpleName = new HashMap<>();

    static {
        simpleName.put(Integer.class, "INTEGER");
        simpleName.put(Byte.class, "byte");
        simpleName.put(Double.class, "double");
        simpleName.put(Float.class, "float");
        simpleName.put(Boolean.class, "boolean");
        simpleName.put(Long.class, "long");
        simpleName.put(Short.class, "short");
        simpleName.put(Character.class, "char");
        simpleName.put(String.class, "string");
        simpleName.put(int.class, "int");
        simpleName.put(double.class, "double");
        simpleName.put(float.class, "float");
        simpleName.put(boolean.class, "boolean");
        simpleName.put(long.class, "long");
        simpleName.put(short.class, "short");
        simpleName.put(char.class, "char");
        simpleName.put(byte.class, "byte");
    }

    public static String getJavaType(Class type) {
        String javaType = simpleName.get(type);
        if (javaType == null) {
            javaType = type.getName();
        }
        return javaType;
    }

    public static final Object current() {
        return EasyOrmSqlBuilder.getInstance();
    }
}