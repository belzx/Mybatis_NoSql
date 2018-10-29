package com.lizhi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

    public static String DateToString(Date date){
        return sdf.format(date);
    }

    public static boolean isNullOrEmpty(String string){
        return string == null || "".equals(string) ? true:false;
    }
}
