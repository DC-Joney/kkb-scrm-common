package com.kkb.common.tx;

import com.kkb.common.pool.WaitThreadPoolExecutor;
import com.kkb.common.pool.annotation.ThreadQueryPool;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * 事务完成之后再执行操作
 */
@ConditionalOnClass(WaitThreadPoolExecutor.class)
public class AfterCommitExecutor implements TransactionSynchronization {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterCommitExecutor.class);
    private static final ThreadLocal<List<Callable<?>>> CALLABLES = ThreadLocal.withInitial(LinkedList::new);

    @Autowired
    @ThreadQueryPool
    private WaitThreadPoolExecutor threadPool;

    public void submit(Callable<?> callable) {

        LOGGER.info("Submitting new callable {} to run after commit", callable);
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            LOGGER.info("Transaction synchronization is NOT ACTIVE. Executing right now runnable ");
            try {
                callable.call();
            } catch (Exception e) {
                LOGGER.info("AfterCommitExecutor error Executing    ", e);
            }
            return;
        }

        List<Callable<?>> threadCallables = CALLABLES.get();

//        if(!TransactionSynchronizationManager.getSynchronizations().contains(this))
//            TransactionSynchronizationManager.registerSynchronization(this);

        //Spring 默认通过 Set 进行存储，所以不会重复添加
        TransactionSynchronizationManager.registerSynchronization(this);

        threadCallables.add(callable);
    }


    /**
     * 添加task 任务，只会影响到当前线程中的事物处理
     *
     * @param transactionTask 任务
     */
    public void addThreadTask(TransactionTask transactionTask) {
        submit(new CallableAdaptor(transactionTask));
    }


    @Override
    public void afterCommit() {
        List<Callable<?>> threadCallables = CALLABLES.get();
        LOGGER.info("Transaction successfully committed, executing {} Callables", threadCallables.size());
        try {
            for (Callable<?> callable : threadCallables) {
                LOGGER.info("Executing callable {}", callable);
                try {
                    threadPool.submitToWait(callable);
                } catch (RuntimeException e) {
                    LOGGER.error("Failed to execute callable " + callable, e);
                }
            }
        } finally {
            threadPool.waitExecutor();
        }
    }

    @Override
    public void afterCompletion(int status) {
        LOGGER.info("Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
        CALLABLES.remove();
    }


    public interface TransactionTask {

        void handle() throws Exception;

    }

    @AllArgsConstructor
    private static class CallableAdaptor implements Callable<Object> {

        private final TransactionTask transactionTask;

        @Override
        public Object call() throws Exception {
            transactionTask.handle();
            return null;
        }
    }

}