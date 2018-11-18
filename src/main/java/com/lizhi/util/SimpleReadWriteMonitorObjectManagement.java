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
    private long avgMonitorTaskTime = 200;


    /**
     * 线修改线程数量
     */
    @Override
    public void monitorInfo(long excutingTime, int monitorMapSize, int outTimeMapSize, int batchAnalysNum, int threadExcutingNum ,int monitorNum) {
        log.info("执行时间：{}ms，监控大小：{}，超时大小：{},一个线程批量处理的数量：{},当前最大线程使用数量：{},本次扫描使用的的线程数量：{},本次监控的对象数量：{}"
                ,excutingTime,monitorMapSize,outTimeMapSize,batchAnalysNum,monitorService.batchAnalysThreadSize,threadExcutingNum,monitorNum);
        if(threadExcutingNum == 0){
            //忽略
        }else if(threadExcutingNum >= 1){
            //获取单个任务执行时间
            long avg = excutingTime/monitorNum;

            //在0到0.3之间浮动不算
            if(avg > avgMonitorTaskTime*1.2){
                int i = monitorService.batchAnalysThreadSize * 3 / 2;
                if(i >AbstractReadWriteMonitorService.MONITOR_POOL_MAX){
                    i = AbstractReadWriteMonitorService.MONITOR_POOL_MAX;
                }
                monitorService.batchAnalysThreadSize  = i;
            }else if (avg < avgMonitorTaskTime * 0.5){
                int i = monitorService.batchAnalysThreadSize * 2 / 3;
                if(i <AbstractReadWriteMonitorService.DEFAULT_EXCUTING_POOL_SIZE){
                    i = AbstractReadWriteMonitorService.DEFAULT_EXCUTING_POOL_SIZE;
                }
                monitorService.batchAnalysThreadSize  = i;
            }
            avgMonitorTaskTime = avg ;
        }
    }

    /**
     * 先把线程数量提高
     * 再把扫描间隔提高
     * @param size
     */
    @Override
    public void putInfo(int size) {
        log.info("put info :{}",size);
       if( ( monitorService.monitorMap.size()+size )/monitorService.batchAnalysNum > monitorService.batchAnalysThreadSize){
           if(monitorService.batchAnalysThreadSize == AbstractReadWriteMonitorService.MONITOR_POOL_MAX){
               if(avgMonitorTaskTime*monitorService.batchAnalysNum > monitorService.monitorDuration){
                    if(monitorService.monitorDuration == AbstractReadWriteMonitorService.MAX_MONITOR_DURATION){
                        //已经是最大值了，则交给阻塞队列
                    }else {
                        monitorService.monitorDuration = AbstractReadWriteMonitorService.MAX_MONITOR_DURATION;
                    }
               }else if(avgMonitorTaskTime*monitorService.batchAnalysNum < AbstractReadWriteMonitorService.DEFAULT_MONITOR_DURATION){
                   monitorService.monitorDuration = AbstractReadWriteMonitorService.DEFAULT_MONITOR_DURATION;
               }
           }else {
               int i = (monitorService.monitorMap.size() + size) / monitorService.batchAnalysNum;
               if(i >AbstractReadWriteMonitorService.MONITOR_POOL_MAX){
                   i = AbstractReadWriteMonitorService.MONITOR_POOL_MAX;
               }
               monitorService.batchAnalysThreadSize = i;
           }
       }
    }
}
