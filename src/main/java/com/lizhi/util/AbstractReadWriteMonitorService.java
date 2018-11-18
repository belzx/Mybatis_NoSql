package com.lizhi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class AbstractReadWriteMonitorService<T> {

    private static Logger log = LoggerFactory.getLogger(AbstractReadWriteMonitorService.class);


    public static final int DEFAULT_ANALYS_NUM = 64;

    public static final int MONITOR_POOL_MAX = 200;

    public static final int DEFAULT_EXCUTING_POOL_SIZE = 10;

    public static final int MONITOR_POOL_MIX = 1;

    public static final int DEFAULT_MONITOR_DURATION = 10 * 1000;

    public static final int MAX_MONITOR_DURATION = 20 * 1000;

    public static final int DEFAULT_GC_MONITOR_DURATION = 10 * 60 * 1000;

    public AbstractReadWriteMonitorObjectManagement management;

    //未过时的保存对象
    protected volatile Map<String, ReadWriteMonitorObject<T>> monitorMap = new HashMap<>();

    //已经过时的保存对象
    protected volatile Map<String, ReadWriteMonitorObject<T>> outTimeMap = new ConcurrentHashMap<>();

    //正在执行扫描的线程数目
    protected volatile AtomicInteger excutimeMonitorThreadNum = new AtomicInteger(0);

    //一个线程最大处理数据的大小
    protected volatile int batchAnalysNum = DEFAULT_ANALYS_NUM;

    //默认处理数据最大的线程数量
    protected volatile int batchAnalysThreadSize = DEFAULT_EXCUTING_POOL_SIZE;

    //gc回收过期的对象的间隔
    private int gcDurationTime = DEFAULT_GC_MONITOR_DURATION;

    //扫描一次的间隔时间，尽量会控制在10s，如果超时，则会有一套算法机制
    protected volatile int monitorDuration = DEFAULT_MONITOR_DURATION;

    //一个读锁顶多只能同时处理100条数据，也就是意味着，
    //超过100条监控的数据，则增加一条读锁
    /**
     * 读写锁,排它锁
     * 允许同时多个线程读取
     * 同时只能一个线程写入
     */
    private volatile ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    /**
     * 缓存线程池
     * 无限长度
     * 100个线程
     */
    private volatile ExecutorService cachedThreadPool = new ThreadPoolExecutor(MONITOR_POOL_MIX, MONITOR_POOL_MAX,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    private Object lockObject = new Object();

    {
        MonitorThreadManager monitorThreadManager = new MonitorThreadManager();
        monitorThreadManager.set(new MonitorThread());
        monitorThreadManager.set(new GcMonitorThread());
        new Thread(monitorThreadManager).start();
    }

    /**
     * 分析对象
     *
     * @param rsubmitObject
     * @return STATUS_NEW 1 新建
     * STATUS_QUEUED 2 排队中
     * STATUS_MONITOING 3 监控中
     * 4 监控成功 一定时间后会被删除 后续会调用successAnayls方法
     * 5 监控失败 一定时间后会被删除 后续会调用errorAnayls方法
     */
    protected abstract int analys(ReadWriteMonitorObject<T> rsubmitObject);

    public void setManagement(AbstractReadWriteMonitorObjectManagement management) {
        this.management = management;
    }

    public void put(List<ReadWriteMonitorObject<T>> data) {
        put(data.stream().collect(Collectors.toMap(ReadWriteMonitorObject::getId, d -> d)));
    }

    public void put(ReadWriteMonitorObject<T> data) {
        Map<String, ReadWriteMonitorObject<T>> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(data.getId(), data);
        put(objectObjectHashMap);
    }

    private void put(Map<String, ReadWriteMonitorObject<T>> data) {
        log.info("添加");
        //添加监控的对象
        try {
            rwl.writeLock().lock();
            Iterator<Map.Entry<String, ReadWriteMonitorObject<T>>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ReadWriteMonitorObject<T>> next = iterator.next();
                if (monitorMap.containsKey(next.getKey()) || outTimeMap.containsKey(next.getKey())) {
                    //不符合要求
                    iterator.remove();
                } else {
                    //可以扫描的对象，则设定好下次扫描的时间
                    next.getValue().setScanTime(System.currentTimeMillis() + next.getValue().getStartTime() * 1000);
                }
            }
            management.putInfo(data.size());
            monitorMap.putAll(data);
        } finally {
            rwl.writeLock().unlock();
        }
        log.info("添加结束");
    }

    public List<ReadWriteMonitorObject<T>> get() {
        log.info("get()'");
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        rwl.readLock().lock();
        try {
            objectObjectHashMap.putAll(monitorMap);
            objectObjectHashMap.putAll(outTimeMap);
        } finally {
            rwl.readLock().unlock();
        }

        List<ReadWriteMonitorObject<T>> collect = objectObjectHashMap.entrySet().stream().map(d -> (ReadWriteMonitorObject<T>) d.getValue()).collect(Collectors.toList());
        log.info("get() over'");
        return collect;
    }

    public ReadWriteMonitorObject<T> get(String id) {
        rwl.readLock().lock();
        ReadWriteMonitorObject<T> result = null;
        try {
            if ((result = monitorMap.get(id)) != null) {
                return result;
            }
        } finally {
            rwl.readLock().unlock();
        }
        return outTimeMap.get(id);
    }

    public void setMonitorObjectScanNow(String id) {
        rwl.writeLock().lock();
        try {
            ReadWriteMonitorObject<T> tReadWriteMonitorObject = monitorMap.get(id);
            if (tReadWriteMonitorObject != null) {
                tReadWriteMonitorObject.setScanTime(System.currentTimeMillis());
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

    //扫描数据
    public void monitor() {
        log.info("扫描开始'");
        long a = System.currentTimeMillis();
        try {
            rwl.writeLock().lock();
            //超时30次，会被判断为error
            //清楚已经完成扫描的的对象
            //最后超时的对象会被垃圾处理器清除
            Iterator<Map.Entry<String, ReadWriteMonitorObject<T>>> iterator = monitorMap.entrySet().iterator();
            while (iterator.hasNext()) {
                ReadWriteMonitorObject<T> value = iterator.next().getValue();

                //修改是否正在此次扫描
                value.setTimer(value.getTimer() + 1);
                value.setMonitoring(value.getTimer() % value.getScanDuration() == 0);

                //没有上锁，并且状态为删除
                //十分钟后删除状态
                if (value.getMonitorTimes() >= ReadWriteMonitorObject.MAX_MONITOR_TIMES &&
                        value.getStatus() != ReadWriteMonitorObject.STATUS_ERROR &&
                        value.getStatus() != ReadWriteMonitorObject.STATUS_SUCCESS
                        ) {
                    value.setStatus(ReadWriteMonitorObject.STATUS_ERROR);
                }

                if (!value.isLock() && value.getStatus() == ReadWriteMonitorObject.STATUS_ERROR) {
                    value.setOutTime(System.currentTimeMillis() + value.getInvidTime() * 1000);
                    outTimeMap.put(value.getId(), value);
                    errorProcess(value);
                    iterator.remove();
                } else if (!value.isLock() && value.getStatus() == ReadWriteMonitorObject.STATUS_SUCCESS) {
                    value.setOutTime(System.currentTimeMillis() + value.getInvidTime() * 1000);
                    outTimeMap.put(value.getId(), value);
                    sucessProcess(value);
                    iterator.remove();
                }
            }
        } finally {
            rwl.writeLock().unlock();
        }


        //本地扫描产生的线程数量
        int threadExcutingNum = 0;

        //一次扫描的对象总量
        int monitorNum = 0;
        //扫描对象
        try {
            log.info("添加如梭");
            rwl.readLock().lock();
            //读取所有的value
            List<ReadWriteMonitorObject<T>> rsubmitObjects = monitorMap.values().stream().
                    filter(d -> System.currentTimeMillis() >= d.getScanTime()).
                    filter(d -> d.isMonitoring()).
                    collect(Collectors.toList());
            int index = 0;
            monitorNum = rsubmitObjects.size();

            //防止数量过多导致崩溃
            if (monitorNum / batchAnalysNum > batchAnalysThreadSize) {
                batchAnalysNum = monitorNum / batchAnalysThreadSize + 1;
            }

            for (; ; ) {
                //分段处理value，50个子任务一组，交给线程池处理
                int endOfindex = index + batchAnalysNum;
                if (endOfindex > monitorNum) {
                    endOfindex = monitorNum;
                }
                List<ReadWriteMonitorObject<T>> rsubmitObjects1 = rsubmitObjects.subList(index, endOfindex);
                if (rsubmitObjects1 != null && !rsubmitObjects1.isEmpty()) {
                    index = endOfindex;
                    excutimeMonitorThreadNum.set(excutimeMonitorThreadNum.get() + 1);
                    threadExcutingNum++;
                    log.info("开始启动线程总数：{}，执行：{}", excutimeMonitorThreadNum, endOfindex);
                    cachedThreadPool.execute(new Task(rsubmitObjects1));
                } else {
                    break;
                }
            }
        } finally {
            log.info("释放如梭");
            rwl.readLock().unlock();
        }

        //确保所有的子线程执行完毕后，才会进行

        synchronized (lockObject) {
            while (true) {
                if (excutimeMonitorThreadNum.get() > 0) {
                    try {
                        lockObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    log.info("所有线程已执行完毕");
                    if (management != null) {
                        management.monitorInfo(System.currentTimeMillis() - a, monitorMap.size(), outTimeMap.size(),
                                batchAnalysNum, threadExcutingNum,monitorNum);
                        log.info("扫描结束,正在扫描中的对象：{},超时对象：{}", monitorMap.size(), outTimeMap.size());
                    }
                    break;
                }
            }
        }
    }


    public void removeOutimeObject() {
        Set<Map.Entry<String, ReadWriteMonitorObject<T>>> entries = outTimeMap.entrySet();
        Iterator<Map.Entry<String, ReadWriteMonitorObject<T>>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            if (System.currentTimeMillis() > iterator.next().getValue().getInvidTime()) {
                iterator.remove();
            }
        }
    }


    class Task implements Runnable {
        private List<ReadWriteMonitorObject<T>> rsubmitObjects;

        Task(List<ReadWriteMonitorObject<T>> rsubmitObjects) {
            this.rsubmitObjects = rsubmitObjects;
        }

        @Override
        public void run() {
            log.info(Thread.currentThread().getName() + "开始执行");
            analysBefor(rsubmitObjects);
            for (ReadWriteMonitorObject<T> rsubmitObject : rsubmitObjects) {
                rsubmitObject.setLock(true);
                try {
                    //分析对象
                    //刷新结果的状态
                    rsubmitObject.setStatus(analys(rsubmitObject));

                    //刷新下一次的扫描时间
//                    rsubmitObject.setScanTime(System.currentTimeMillis() + rsubmitObject.getScanDuration() * 60 * 1000);

                    //刷新监控次数
                    rsubmitObject.setMonitorTimes(rsubmitObject.getMonitorTimes() + 1);

                    //刷新计时器转台
                    rsubmitObject.setMonitoring(false);
                } finally {
                    rsubmitObject.setLock(false);
                }
            }

            //修改状态
            log.info(Thread.currentThread().getName() + "执行完毕");
            excutimeMonitorThreadNum.set(excutimeMonitorThreadNum.get() - 1);
            synchronized (lockObject) {
                lockObject.notifyAll();
            }

        }
    }

    /**
     * 管理器，保证线程不会挂
     * 挂了后及时重启
     */
    class MonitorThreadManager implements Runnable {
        List<Runnable> threads = new ArrayList<>();

        public void set(Runnable thread) {
            threads.add(thread);
        }

        @Override
        public void run() {
            for (Runnable thread : threads) {
                new Thread(() -> {
                    while (true) {
                        Thread thread1 = new Thread(thread);
                        thread1.start();
                        try {
                            thread1.join();
                        } catch (InterruptedException e) {
                            log.error("线程中断:{}", thread1.getName());
                        }
                    }
                }).start();
            }
        }
    }

    class MonitorThread implements Runnable {
        @Override
        public void run() {
            log.info("监控线程开启");
            while (true) {
                long a = System.currentTimeMillis();
                monitor();
                long monitorSpendTime = System.currentTimeMillis() - a;

                //开始保证10s的扫描间隔时间
                if (monitorSpendTime >= monitorDuration) {
                    log.warn("扫描一次对象的时间超过了" + monitorDuration + "ms");
                } else {
                    try {
                        Thread.sleep(monitorDuration - monitorSpendTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class GcMonitorThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    removeOutimeObject();
                    Thread.sleep(gcDurationTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void analysBefor(List<ReadWriteMonitorObject<T>> rsubmitObjects) {

    }

    private void sucessProcess(ReadWriteMonitorObject<T> value) {
    }

    private void errorProcess(ReadWriteMonitorObject<T> value) {
    }


}
