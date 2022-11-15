package com.kkb.common.pool;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author chixin
 * @version 1.0
 * @date 2021/7/16 5:56 下午
 */
@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class WaitThreadPoolExecutor implements ExecutorService {
    public static ThreadLocal<List<Future<?>>> futureLocals = ThreadLocal.withInitial(Lists::newLinkedList);

    @Delegate
    private ExecutorService digest;

    public WaitThreadPoolExecutor(ExecutorService executorService) {
        this.digest = executorService;
    }

    //调用submitToWait和waitExecutor配套使用，调用submitToWait后必须调用waitExecutor。否则会内存泄漏
    public <T> Future<T> submitToWait(Callable<T> task) {
        List<Future<?>> futures = futureLocals.get();
        Future<T> submit = digest.submit(task);
        futures.add(submit);
        return submit;
    }

    public void waitExecutor() {
        List<Future<?>> futures = futureLocals.get();
        if (CollectionUtils.isEmpty(futures)) {
            return;
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("ScrmThreadPoolExecutor:", e);
            }
        }
        futureLocals.remove();
    }

}
