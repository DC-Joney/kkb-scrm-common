package com.kkb.common.pool;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.kkb.common.pool.annotation.ThreadQueryPool;
import com.kkb.common.tx.AfterCommitExecutor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.kkb.common.pool.annotation.ThreadBusinessPool.ASYNC_THREAD_POOL_BUSi_DEAL;


/**
 * 线程池配置
 * @author xchi
 */
@ConditionalOnClass(WaitThreadPoolExecutor.class)
@EnableAsync
public class ThreadPoolConfig {


    @Bean(ASYNC_THREAD_POOL_BUSi_DEAL)
    public WaitThreadPoolExecutor asyncThreadPoolExecutor(BeanFactory beanFactory) {
        int i = Runtime.getRuntime().availableProcessors()*2;
        ExecutorService executorService = new ThreadPoolExecutor(i, i,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        executorService= TtlExecutors.getTtlExecutorService(executorService);
        executorService = new TraceableExecutorService(beanFactory, executorService);
        return new WaitThreadPoolExecutor(executorService);
    }


    @Bean
    @ThreadQueryPool
    public WaitThreadPoolExecutor queryAsyncThreadPoolExecutor(BeanFactory beanFactory) {
        int i = Runtime.getRuntime().availableProcessors() * 10;
        ExecutorService executorService = new ThreadPoolExecutor(i, i,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        executorService= TtlExecutors.getTtlExecutorService(executorService);
        executorService = new TraceableExecutorService(beanFactory, executorService);
        return new WaitThreadPoolExecutor(executorService);
    }

    @Bean
    public AfterCommitExecutor afterCommitExecutor(){
        return new AfterCommitExecutor();
    }


}



