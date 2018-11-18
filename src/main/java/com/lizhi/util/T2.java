package com.lizhi.util;

import com.lizhi.annotions.ClassExport;

@ClassExport(beanName = "t2")
public class T2 {

    @ClassExport(fieldName = "你123好213",ignoreBeaName = {"t1"})
    private String ss1123 = "kk231321";

    @ClassExport(fieldName = "你好")
    private String ss = "kk";


    @ClassExport(fieldName = "你123好")
    private String ss1 = "kk";


}
