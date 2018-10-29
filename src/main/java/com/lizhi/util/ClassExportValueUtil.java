package com.lizhi.util;

import com.lizhi.annotions.ClassExport;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 导出一个类中所有被@ExportValue标记的所有成员变量的key value值
 */
public class ClassExportValueUtil<T> {

    /**保存变量名*/
    /**
     * beanName - beanName下面所有变量名称
     */
    private Map<String, List<String>> variableNames = new HashMap<>();

    /**保存时子变量的fiedName*/
    /**
     * beanName . fiedName
     */
    private List<String> isSubpropertys = new ArrayList<>();

    /**
     * 检验list参数类型是否统一
     */
    private Class nowClass = null;

    /**
     * 最顶级扫描的beanName
     */
    private String superBeanName = "";

    public List<List<String>> processObjects(List<T> objects, Class<T> clazz) {
        List<List<String>> result = new ArrayList<>();

        try {
            if (objects.isEmpty()) {
                objects.add(clazz.newInstance());
                result.add(getFieldName(objects.get(0), getBeanName(objects.get(0))));
            } else {
                result.add(getFieldName(objects.get(0), getBeanName(objects.get(0))));
                result.addAll(getFieldValues(objects));
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    public static <T> List<List<String>> getReflectValues(List<T> objects, Class<T> clazz) {

        return new ClassExportValueUtil().processObjects(objects, clazz);
    }


    /**
     * 得到成员属性的名称
     *
     * @return
     */
    public String getBeanName(Object object) {
        ClassExport annotation = object.getClass().getAnnotation(ClassExport.class);
        if (annotation == null) {
            throw new RuntimeException("对象类注解不包含com.lizhi.annotions.ExcelExport");
        }

        String beanName = annotation.beanName();

        if (StringUtils.isNullOrEmpty(superBeanName)) superBeanName = beanName;

        return beanName;
    }

    /**
     * 得到成员属性的名称
     *
     * @return
     */
    public List<String> getFieldName(Object o, String beanName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        List<String> fieldNames = new ArrayList<>();
        List<Field> subproperts = new ArrayList<>();
        Field[] declaredFields = o.getClass().getDeclaredFields();

        if (declaredFields.length == 0) {
            return new ArrayList<>();
        }

        List<String> variableName = new ArrayList<>();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            ClassExport declaredAnnotation = field.getDeclaredAnnotation(ClassExport.class);

            if (declaredAnnotation == null) continue;

            if (StringUtils.isNullOrEmpty(declaredAnnotation.fieldName())) {
                if (declaredAnnotation.isSubproperty()) {
                    subproperts.add(field);
                    isSubpropertys.add(beanName + "." + field.getName());
                    variableName.add(field.getName());
                } else {
                    throw new RuntimeException("@ExcelExport注解在成员变量上时，fieldname不能为空");
                }
            } else {
                if (!Arrays.asList(declaredAnnotation.ignoreBeaName()).contains(superBeanName)) {
                    if (fieldNames.contains(beanName + "." + declaredAnnotation.fieldName())) {
                        throw new RuntimeException("@ExcelExport的field在同一个类中不能有重复的fieldName:" + declaredAnnotation.fieldName());
                    }
                    fieldNames.add(beanName + "." + declaredAnnotation.fieldName());
                    variableName.add(field.getName());
                }
            }
        }

        variableNames.put(beanName, variableName);

        for (Field fie : subproperts) {
            Object nObject = fie.get(o);
            if (nObject == null) {
                Class<?> aClass = null;
                nObject = Class.forName(fie.getType().getTypeName()).newInstance();
            }
            String subBeanName = getBeanName(nObject);
            fieldNames.addAll(getFieldName(nObject, subBeanName));
        }

        return fieldNames;
    }

    public List<List<String>> getFieldValues(List objects) throws NoSuchFieldException, IllegalAccessException {
        List<List<String>> result = new ArrayList<>();

        for (Object object : objects) {
            if (object == null) {
                result.add(null);
                continue;
            }

            if (nowClass == null) {
                nowClass = object.getClass();
            }

            if (nowClass != object.getClass()) {
                throw new RuntimeException("list中数据类型没有保持统一" + nowClass.getName() + "error to match " + object.getClass().getName());
            }

            result.add(getFieldValue(object));
        }

        return result;
    }

    public List<String> getFieldValue(Object object) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        List<String> result = new ArrayList<>();
        List subproperts = new ArrayList<>();
        String beanName = getBeanName(object);
        List<String> list = variableNames.get(beanName);
        for (String fieldNameFo : list) {
            Field declaredField = clazz.getDeclaredField(fieldNameFo);
            declaredField.setAccessible(true);

            if (isSubpropertys.contains(beanName + "." + fieldNameFo)) {
                subproperts.add(declaredField.get(object));
                continue;
            }

            Object o = declaredField.get(object);
            if (o == null) {
                result.add("");
            } else if (o instanceof Date) {
                result.add(StringUtils.DateToString((Date) o));
            } else {
                result.add(o.toString());
            }
        }

        for (Object obj : subproperts) {
            if (obj == null) continue;
            result.addAll(getFieldValue(obj));
        }

        return result;
    }


//    public static void main(String[] args) {
//        T1 t1 = new T1();
//        t1.setT2(new T2());
//        List<T1> ll = new ArrayList<>();
//        ll.add(t1);
//        ll.add(null);
//        ll.add(new T1());
//        List<List<String>> reflectValues = new ClassExportValueUtil<T1>().processObjects(ll, T1.class);
//        System.out.println(reflectValues);
//    }
}
