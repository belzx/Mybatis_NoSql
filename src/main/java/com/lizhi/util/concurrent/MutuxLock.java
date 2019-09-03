package com.lizhi.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 同时保证几个线程同时执行，跟信号量有点相似
 *
 * @author lizhixiong
 * @time 2019 - 09 - 03 - 21:22
 */
public class MutuxLock {
    private volatile Sync sync;

    public MutuxLock() {
        sync = new Sync(1);
    }

    public MutuxLock(int i) {
        sync = new Sync(i);
    }

    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    public boolean unlock() {
        return sync.release(1);
    }

    public boolean tryWaitLock(int outTime) throws InterruptedException {
        return sync.tryAcquireNanos(1, outTime * 1000000L);
    }

    public static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 3067675380035896676L;
        int maxThread = 1;

        public Sync(int maxThread) {
            this.maxThread = maxThread;
        }

        @Override
        protected boolean tryAcquire(int arg) {
            if (getState() < maxThread && compareAndSetState(getState(), getState() + 1)) {
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() > 0 && compareAndSetState(getState(), getState() - 1)) {
                return true;
            }
            return false;
        }
    }
}
