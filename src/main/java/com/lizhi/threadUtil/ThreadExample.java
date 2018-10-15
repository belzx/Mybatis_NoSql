package com.lizhi.threadUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ThreadExample {

    public static void main(String[] args) {
        // new ThreadExample().productToComsumer();
//        new ThreadExample().productToComsumers();
        new ThreadExample().productToComsumers();
    }

    volatile boolean hasProduct = true;

    /**
     * 1-1 生产者 消费者模式
     */
    public void productToComsumer() {
        Object o = new Object();
        new Thread(() -> {//生产者
            while (true) {
                synchronized (o) {
                    if (!hasProduct) {
                        hasProduct = true;
                        //开始生产
                        System.out.println("生产了一个");
                        o.notifyAll();
                    }
                    try {
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();

        new Thread(() -> {//消费者
            while (true) {
                synchronized (o) {
                    if (hasProduct) {
                        hasProduct = false;
                        //开始生产
                        System.out.println("消费了一个");
                        o.notifyAll();
                    }
                    try {
                        o.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 1-N 生产者 消费者模式
     */
    public void productToComsumers() {
        final Queue<Integer> ductors = new LinkedList<>();
        new Thread(() -> {//生产者
            while (true) {
                synchronized (ductors) {
                    if (ductors.size() == 0) {
                        //开始生产
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int i1 = (int) (Math.random() * 10)+1;
                        System.out.println("生产了" + i1 + "个");
                        for (int i = 0; i < i1; i++) {
                            ductors.add(i);
                        }
                        ductors.notifyAll();
                    }
                    try {
                        ductors.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> {//消费者
                while (true) {
                    synchronized (ductors) {
                        if (ductors.size()>0) {
                            //开始消费
                            System.out.println("线程" + Thread.currentThread().getName() + "消费了一个");
                            ductors.poll();
                            ductors.notifyAll();
                        }
                        try {
                            ductors.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.setName(i + "");
            threads.add(t);
        }

        threads.forEach(d -> {
            d.start();
        });
    }

}
