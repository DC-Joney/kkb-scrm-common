package com.kkb.common.tools.concurrent.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 多线程异常收集器
 *
 * @author zhangyang
 * @date 2020-07-31
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PACKAGE)
public class ExceptionManager {

    /**
     * 所有线程共享同一个异常，如果异常相同，则累加
     */
    @Getter
    volatile Throwable errorBefore;

    static final AtomicReferenceFieldUpdater<ExceptionManager, Throwable> TRX_BEFORE =
            AtomicReferenceFieldUpdater.newUpdater(ExceptionManager.class, Throwable.class, "errorBefore");


    /**
     * 所有线程共享同一个异常，如果异常相同，则累加
     */
    @Getter
    volatile Throwable errorAfter;

    static final AtomicReferenceFieldUpdater<ExceptionManager, Throwable> TRX_AFTER =
            AtomicReferenceFieldUpdater.newUpdater(ExceptionManager.class, Throwable.class, "errorAfter");

    @Getter
    volatile int parties;

    static final AtomicIntegerFieldUpdater<ExceptionManager> P =
            AtomicIntegerFieldUpdater.newUpdater(ExceptionManager.class, "parties");


    /**
     * 自旋添加所有异常
     *
     * @param exception
     * @return
     */
    public void addExceptionBefore(Throwable exception) {
        addThrowable(TRX_BEFORE, this, exception);
    }


    /**
     * 自旋添加所有异常
     *
     * @param exception
     * @return
     */
    public <T> T addExceptionBeforeReturn(Throwable exception, T returnValue) {
        addThrowable(TRX_BEFORE, this, exception);
        return returnValue;
    }

    /**
     * 自旋添加所有异常
     */
    public void addExceptionAfter(Throwable exception) {
        addThrowable(TRX_AFTER, this, exception);
    }


    /**
     * 添加许可
     *
     * @param p
     */
    void addParties(int p) {
        for (; ; ) {
            int current = getParties();
            int update = current + p;
            if (P.compareAndSet(this, current, update)) {
                return;
            }
        }
    }


    /**
     * 自旋添加异常，如果该异常已经初始化则将异常进行累加
     *
     * @param field     异常字段
     * @param instance  当前实例
     * @param exception 异常实例
     */
    private static <T> void addThrowable(AtomicReferenceFieldUpdater<T, Throwable> field,
                                         T instance,
                                         Throwable exception) {
        for (; ; ) {
            Throwable current = field.get(instance);

            if (current instanceof CompositeException) {
                //this is ok, composite exceptions are never singletons
                current.addSuppressed(exception);
                break;
            }

            Throwable update;
            if (current == null) {
                update = exception;
            } else {
                update = multiple(current, exception);
            }

            if (field.compareAndSet(instance, current, update))
                break;

        }
    }

    private static Throwable multiple(Throwable... throwables) {
        CompositeException multiple = new CompositeException();
        //noinspection ConstantConditions
        if (throwables != null) {
            for (Throwable t : throwables) {
                //this is ok, multiple is always a new non-singleton instance
                multiple.addSuppressed(t);
            }
        }
        return multiple;
    }


    public boolean hasErrorBefore() {
        return errorBefore != null;
    }
}
