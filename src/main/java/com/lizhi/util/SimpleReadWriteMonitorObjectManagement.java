package com.lizhi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该管理器的作用
 * 1：尽量保证在合规的扫描时间内
 * 2：防止数据量太大，导致过载
 */
public class SimpleReadWriteMonitorObjectManagement extends  AbstractReadWriteMonitorObjectManagement {

    private static Logger log = LoggerFactory.getLogger(SimpleReadWriteMonitorObjectManagement.class);

    /**
     * 一个子任务平均的执行时间
     */
    private long avgMonitorTaskTime;

    @Override
    public void monitorInfo(long excutingTime, int monitorMapSize, int outTimeMapSize, int batchAnalysNum, int threadExcutingNum ,int monitorNum) {
        log.info("执行时间：{}ms，监控大小：{}，超时大小：{},一个线程批量处理的数量：{},当前最大线程使用数量：{},本次扫描使用的的线程数量：{},本次监控的对象数量：{}"
                ,excutingTime,monitorMapSize,outTimeMapSize,batchAnalysNum,monitorService.batchAnalysThreadSize,threadExcutingNum,monitorNum);
        if(threadExcutingNum == 0){
            //忽略
        }else if(threadExcutingNum == 1){
            //获取单个任务执行时间
            long avg = excutingTime/monitorNum;
            if(avg > avgMonitorTaskTime){

            }else if (avg < avgMonitorTaskTime){

            }else {

            }
            int batchAnalysThreadSize = monitorService.batchAnalysNum;
            //
        }
    }

    @Override
    public void putInfo(int size) {
        log.info("put info :{}",size);
    }
}
