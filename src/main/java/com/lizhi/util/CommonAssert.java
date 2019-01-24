package com.lizhi.util;

import org.springframework.util.Assert;

public class CommonAssert extends Assert {
    public static void notNullOrEmpty(String str,String message){
        if(str == null || str.length() == 0){
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNullOrEmpty(String str){
        notNullOrEmpty( str, "can not be null or empty");
    }
}
