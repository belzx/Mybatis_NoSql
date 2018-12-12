package com.lizhi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class StreamUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(StreamUtils.class);

    public static void close(Closeable ... closeables){
        for(Closeable closeable : closeables){
            if(closeable != null){
                try {
                    closeable.close();
                }catch (Exception e){
                    LOGGER.error("",e);
                }
            }
        }
    }
}
