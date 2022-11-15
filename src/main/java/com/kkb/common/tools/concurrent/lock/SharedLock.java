package com.kkb.common.tools.concurrent.lock;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;

/**
 * 共享锁的添加和 解锁、不支持重入
 *
 * @author zhangyang
 * @date 2020-09-08
 * @see AbstractQueuedSynchronizer
 */
@NotThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SharedLock extends AbstractQueuedSynchronizer {

    private static final int LOCK_STATE = 1;
    private static final int UNLOCK_STATE = -1;

    /**
     * 正在解锁状态
     */
    private static final int UNLOCKING = 0;

    /**
     * 用于表示 在 CHL队列中的线程
     */
    private ThreadLocal<Boolean> CHL = ThreadLocal.withInitial(() -> false);

    /**
     * 用于存放队列解锁时，尝试加锁的线程
     */
    private final ConcurrentLinkedDeque<Thread> threadQueue = new ConcurrentLinkedDeque<>();

    public SharedLock() {
        setState(UNLOCK_STATE);
    }


    @Override
    protected int tryAcquireShared(int arg) {

        for (; ; ) {

            int state = getState();

            //将加入CHL队列的线程打标记
            if (state == LOCK_STATE) {
                CHL.set(true);
                return -1;
            }

            //如果是在CHL队列中的线程则直接放行
            if (state == UNLOCKING && CHL.get()) {
                return 1;
            }

            //如果是在解锁的时候进入的线程，则阻塞等待，直到解锁完成
            if (state == UNLOCKING && !CHL.get()) {
                Thread thread = Thread.currentThread();
                threadQueue.push(thread);
                LockSupport.park(thread);
            }

            if (compareAndSetState(UNLOCK_STATE, LOCK_STATE)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return getState();
            }
        }
    }


    @Override
    protected boolean tryReleaseShared(int arg) {
        setState(UNLOCKING);
        setExclusiveOwnerThread(null);
        return true;
    }


    /**
     * 加共享锁
     */
    public void lockShare() {
        acquireShared(1);
    }

    /**
     * 解锁
     */
    public void unlock() {

        if (isLocked()) {
            releaseShared(1);
        }

        //在CHL队列解锁完成后，将解锁时尝试加锁的线程进行唤醒
        if (getQueueLength() == 0) {

            //唤醒阻塞线程时将状态设置为可获取状态
            setState(UNLOCK_STATE);

            for (; ; ) {
                try {
                    Thread thread = threadQueue.pollLast();

                    //如果 thread 为空则表示 队列中已经没有等待的线程
                    if (thread == null) {
                        break;
                    }

                    //解锁该线程
                    LockSupport.unpark(thread);

                } catch (Exception e) {
                    //TODO doNothing
                }
            }
        }

        //将解锁完成的线程 移除CHL标记
        CHL.remove();
    }

    /**
     * 查看是否是加锁状态
     *
     * @return
     */
    public boolean isLocked() {
        return getState() != 0;
    }


    /**
     * 查看当前线程是否拥有锁
     */
    public boolean isLockedBySelf() {
        return getExclusiveOwnerThread() == Thread.currentThread();
    }


    /**
     * 不响应中断的添加共享锁
     *
     * @param time     时间
     * @param timeUnit 时间单位
     */
    public void lockShare(long time, TimeUnit timeUnit) throws InterruptedException {
        long waitTime, acquireTime;

        //需要等待的时间
        waitTime = acquireTime = timeUnit.toNanos(time);

        //开始时间
        long startTime = System.nanoTime();

        //结束时间
        long endTime = startTime + acquireTime;

        do {

            try {
                tryAcquireSharedNanos(1, waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                waitTime = Math.max(acquireTime + startTime - System.nanoTime(), 0);
            }

        } while (endTime > System.nanoTime());
    }
}
