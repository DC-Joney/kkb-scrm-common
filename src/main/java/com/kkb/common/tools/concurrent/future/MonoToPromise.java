package com.kkb.common.tools.concurrent.future;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Operators;

import java.util.concurrent.atomic.AtomicReference;

public class MonoToPromise<T> extends DefaultPromise<T> implements CoreSubscriber<T> {

    final AtomicReference<Subscription> ref = new AtomicReference<>();

    public MonoToPromise(EventExecutor executor) {
        super(executor);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = super.cancel(mayInterruptIfRunning);
        if (cancelled) {
            Subscription s = (Subscription) this.ref.getAndSet(null);
            if (s != null) {
                s.cancel();
            }
        }

        return cancelled;
    }

    public void onSubscribe(Subscription s) {
        if (Operators.validate(this.ref.getAndSet(s), s)) {
            s.request(Integer.MAX_VALUE);
        } else {
            s.cancel();
        }

    }

    public void onNext(T t) {
        Subscription s = this.ref.getAndSet(null);
        if (s != null) {
            this.trySuccess(t);
            s.cancel();
        } else {
            Operators.onNextDropped(t, this.currentContext());
        }

    }

    public void onError(Throwable t) {
        if (this.ref.getAndSet( null) != null) {
            this.tryFailure(t);
        }

    }

    public void onComplete() {
        if (this.ref.getAndSet(null) != null) {
            this.trySuccess( null);
        }

    }



}


