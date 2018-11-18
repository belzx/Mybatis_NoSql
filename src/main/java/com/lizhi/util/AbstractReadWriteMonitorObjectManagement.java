package com.lizhi.util;

/**
 * 管理器
 */
public abstract class AbstractReadWriteMonitorObjectManagement {

    AbstractReadWriteMonitorService monitorService ;

    public void set(AbstractReadWriteMonitorService monitorService ){
        this.monitorService = monitorService;
        monitorService.setManagement(this);
    }

    public abstract void monitorInfo(long excutingTime, int monitorMapSize, int outTimeMapSize, int batchAnalysNum ,int threadExcutingNum , int monitorNum);

    public abstract void putInfo(int size);
}
