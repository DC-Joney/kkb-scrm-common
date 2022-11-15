package com.kkb.common.tools.concurrent.future;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class ListenableThreadPoolExecutor extends ThreadPoolExecutor {

    public ListenableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public <T> FutureAdaptor<T> newTaskFor(Callable<T> callable) {
        return callable instanceof FutureAdaptor ? (FutureAdaptor<T>) callable : new FutureAdaptor<>(callable);
    }

    @Override
    public <T> FutureAdaptor<T> newTaskFor(Runnable runnable, T value) {
        return runnable instanceof FutureAdaptor ? (FutureAdaptor<T>) runnable : new FutureAdaptor<>(runnable, value);
    }

    public <T> FutureAdaptor<T> newTaskFor(Runnable runnable) {
        return runnable instanceof FutureAdaptor ? (FutureAdaptor<T>) runnable : new FutureAdaptor<>(runnable ,null);
    }
}
