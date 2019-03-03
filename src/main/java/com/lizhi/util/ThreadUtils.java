package com.lizhi.util;

import java.util.concurrent.*;

public class ThreadUtils {
    public static ExecutorService newCachedThreadPool(String poolName){
        return   new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),new SimpleThreadFactory(poolName));
    }
}
