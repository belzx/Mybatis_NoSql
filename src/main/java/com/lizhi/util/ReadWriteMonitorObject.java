package com.lizhi.util;

import java.util.UUID;

public class ReadWriteMonitorObject<T> {

    public static final int STATUS_NEW = 1;

    public static final int STATUS_MONITOING = 2;

    public static final int STATUS_SUCCESS= 3;

    public static final int STATUS_ERROR = 4;

    /**
     * 最大扫描次数
     * 超过则算作error状态
     */
    public static final int MAX_MONITOR_TIMES = 30;

    /**
     * 内存保存的唯一标识
     */
    private String id;

    /**
     * 保存的对象
     */
    private T data;

    /**
     * 扫描的间隔
     * 以s为单位：最好为10的整数
     * 1  表示为AbstractReadWriteMonitorService.monitorDuration *1
     * 2  表示为AbstractReadWriteMonitorService.monitorDuration *2
     * ..
     * ..
     */
    private int scanDuration;

    /**
     * 计时器，配合scanDuration使用的
     * 即 monitor线程的扫描次数
     */
    private int timer;

    /**
     * 是否正在扫描
     */
    private boolean monitoring;

    /**
     * 第一次扫描的时间(时间戳)
     */
    private long scanTime;

    /**
     * 开始扫描的时间(时间搓)
     */
    private int startTime;

    /**
     * 监控的的次数
     */
    private int monitorTimes = 0;

    /**
     * 对象失效时间(s)
     * 一个对象完成扫描后，则xxmin内不会再监控此对象
     */
    private int invidTime;

    /**
     *超时时间，由当前时间+invidTime决定
     */
    private long outTime;

    /**
     * 单位锁
     * true ,表示正在被扫描处理当中
     * false
     */
    private boolean lock;

    /**
     * 0状态会被删除
     */
    private int status = STATUS_NEW;

    public ReadWriteMonitorObject(int startTime, int scanDuration ,int invidTime, T data) {
        this.id = UUID.randomUUID().toString();
        this.data = data;
        this.scanDuration = scanDuration;
        this.startTime = startTime;
        this.invidTime = invidTime;
    }

    //id 开始时间(s)  间隔(s) 失效时间(s) 对象
    public ReadWriteMonitorObject(String id, int startTime, int scanDuration ,int invidTime, T data) {
        this.id = id;
        this.data = data;
        this.scanDuration = scanDuration;
        this.startTime = startTime;
        this.invidTime = invidTime;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getMonitorTimes() {
        return monitorTimes;
    }

    public void setMonitorTimes(int monitorTimes) {
        this.monitorTimes = monitorTimes;
    }

    public int getInvidTime() {
        return invidTime;
    }

    public void setInvidTime(int invidTime) {
        this.invidTime = invidTime;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getScanDuration() {
        return scanDuration;
    }

    public void setScanDuration(int scanDuration) {
        this.scanDuration = scanDuration;
    }

    public long getScanTime() {
        return scanTime;
    }

    public void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }
}