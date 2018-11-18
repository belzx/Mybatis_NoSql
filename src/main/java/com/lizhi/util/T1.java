package com.lizhi.util;

import com.lizhi.annotions.ClassExport;

@ClassExport(beanName = "t1")
public class T1 {

    @ClassExport(fieldName = "你好")
    private String ss = "123123";

    @ClassExport(isSubproperty = true)
    private T2 t2 ;

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public T2 getT2() {
        return t2;
    }

    public void setT2(T2 t2) {
        this.t2 = t2;
    }
}
