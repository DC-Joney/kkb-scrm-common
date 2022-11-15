package com.kkb.common.tools.concurrent.future;


import com.kkb.common.tools.concurrent.collection.ConcurrentList;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureAdaptor<T> extends FutureTask<T> {

    private static final int SUCCESS = 1;
    private static final int EXCEPTIONAL = -1;
    private static final int INTERRUPTED = -2;

    private volatile Throwable exception;

    private volatile T result;

    private ConcurrentList<GenericListener<T>> listeners = new ConcurrentList<>();

    private volatile int state = 0;

    public FutureAdaptor(Callable<T> callable) {
        super(callable);
    }

    public FutureAdaptor(Runnable runnable, T result) {
        super(runnable, result);
    }

    @Override
    public void run() {
        super.run();
    }

    public boolean isSuccess() {
        return !isCancelled() && exception == null && state > 0;
    }

    public FutureAdaptor<T> addListener(GenericListener<T> listener){
        listeners.add(listener);
        return this;
    }

    public Throwable getCause() {
        return exception;
    }

    public T getResult() {
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel = super.cancel(mayInterruptIfRunning);
        if (cancel)
            state = INTERRUPTED;
        return cancel;
    }

    @Override
    protected void done() {
        Throwable cause;
        try {
            result = get();
            state = SUCCESS;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            state = INTERRUPTED;
        } catch (ExecutionException ex) {
            cause = ex.getCause();
            if (cause != null) {
                state = EXCEPTIONAL;
                cause = ex;
                exception = cause;
            }
        } catch (Throwable ex) {
            cause = ex;
            exception = cause;
            state = EXCEPTIONAL;
        }

        listeners.forEach(listener -> listener.onComplete(this));
    }
}
