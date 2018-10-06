package com.lizhi.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanTransUtil {



    public static Map<String, Object> convertObjToMap(Object obj) {
        Map<String, Object> reMap = new HashMap<String, Object>();
        if (obj == null)
            return null;
        try {
            Class<?> objClass = obj.getClass();
            while (objClass != null) {
                Field[] fields = objClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Field f = objClass.getDeclaredField(fields[i].getName());
                        f.setAccessible(true);
                        Object o = f.get(obj);
                        reMap.put(fields[i].getName(), o);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                objClass = objClass.getSuperclass();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return reMap;
    }
}
