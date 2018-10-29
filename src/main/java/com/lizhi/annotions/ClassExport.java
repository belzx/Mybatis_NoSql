package com.lizhi.annotions;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD})//确定可使用的作用域
@Retention(RUNTIME)
@Documented
public @interface ClassExport {
    /**
     * 修饰class ，描述class的名称
     * @return
     */
    String beanName() default "";

    /**
     * 修饰成员变量
     * @return
     */
    String fieldName() default "";

    /**
     * 标记是否继续向下扫描@ClassExport
     */
    boolean isSubproperty() default false;

    /**
     * 值为第一层的bean时，不会被导出；
     */
    String[] ignoreBeaName() default "";
}
