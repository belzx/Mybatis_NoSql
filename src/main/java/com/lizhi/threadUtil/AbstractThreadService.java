//package com.lizhi.threadUtil;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
///**
// * 写的一些小例子
// * @param <T>
// */
//public abstract class AbstractThreadService<T> {
//
//    public volatile Queue<T> lists = new LinkedList<>();
//
//    public int consumerNum = 5;
//
//    /**
//     * 1-N 生产者 消费者模式。队列模式
//     */
//    public void productToComsumers() {
//        ArrayList<Thread> threads = new ArrayList<>();
//        Thread thread = new Thread(() -> {//生产者
//            while (true) {
//                synchronized (lists) {
//                    if (lists.size() == 0) {
//                        //开始生产
//                        List<T> product = product();
//                        System.out.println("线程" + Thread.currentThread().getName() + "生产了："+product.size()+"个对象");
//                        lists.addAll(product);
//                    }
//                    try {
//                        lists.notifyAll();
//                        lists.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        threads.add(thread);
//
//        for (int i = 0; i < consumerNum; i++) {
//            Thread t = new Thread(() -> {//消费者
//                while (true) {
//                    synchronized (lists) {
//                        if (lists.size()>0) {
//                            //开始消费
//                            System.out.println("线程" + Thread.currentThread().getName() + "开始消费了一个");
//                            consumer(lists.poll());
//                        }
//                        try {
//                            lists.notifyAll();
//                            lists.wait();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            t.setName("消费者线程"+i);
//            threads.add(t);
//        }
//
//        threads.forEach(d -> {
//            d.start();
//        });
//    }
//
//    abstract void consumer(T poll) ;
//
//    /**
//     * 生产 返回值不能为空
//     * @return
//     */
//    abstract List<T> product();
//}
