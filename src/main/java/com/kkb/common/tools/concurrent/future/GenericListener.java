package com.kkb.common.tools.concurrent.future;

public interface GenericListener<T> {

    void onComplete(FutureAdaptor<T> future);
}
