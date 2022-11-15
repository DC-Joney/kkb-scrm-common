package com.kkb.common.tools.concurrent.record;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@NotThreadSafe
@SuppressWarnings("all")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConcurrentTaskRecord {

    /**
     * 已启动状态
     */
    static final int STARTED = 1;

    /**
     * 初始状态
     */
    static final int NEW = 0;

    /**
     * 停止状态
     */
    static final int STOP = -1;

    /**
     * 表示正在停止 状态
     */
    static final int STOPPING = -2;


    /**
     * 是否是停止状态
     */
    static boolean IsStop(int state) {
        return state < 0;
    }

    /**
     * 是否是开始状态
     */
    static boolean IsStarted(int state) {
        return state > 0;
    }


    /**
     * 用于存放所有任务的数据
     */
    final ConcurrentHashMap<Thread, MetricCollector> taskMap;
    final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 已经完成的任务数量
     */
    final LongAdder completeTasks = new LongAdder();

    /**
     * 当前记录仪的状态
     */
    @Getter
    volatile int state;

    static final AtomicIntegerFieldUpdater<ConcurrentTaskRecord> STATE =
            AtomicIntegerFieldUpdater.newUpdater(ConcurrentTaskRecord.class, "state");

    /**
     * 开始时间
     */
    @Getter
    volatile long startTime;

    /**
     * 结束时间
     */
    @Getter
    volatile long endTime;


    @Getter
    final Lock lock = new ReentrantLock();

    @Getter
    final Condition completeCondition = lock.newCondition();

    volatile boolean counterState = false;

    /**
     * @param bucket     拆分的桶数量
     * @param threadSize 任务所需要的线程数量
     */
    public ConcurrentTaskRecord(int initSize) {
        this.taskMap = new ConcurrentHashMap<>(initSize);
    }


    //动态的在多个线程进行操作
    private void start() {
        for (; ; ) {
            if (getState() > 0 || getState() < 0) {
                return;
            }
            //这里会有延迟机制，但是不会有太大影响
            if (STATE.compareAndSet(this, NEW, STARTED)) {
                this.startTime = System.currentTimeMillis();
            }
        }

    }


    /**
     * 创建一个新的 Task任务
     *
     * @param taskName 任务名称
     */
    public RecordTask newRecord(String taskName) {

        //如果当前的记录器还没有开始则开始
        if (!isStarted()) {
            this.start();
        }

        //如果当前记录器已经是stop 状态则不允许再添加task
        if (isStop()) {
            return RecordTask.EMPTY;
        }

        Thread thread = Thread.currentThread();

        MetricCollector taskManager = taskMap.get(thread);

        if (taskManager == null) {
            this.counter.incrementAndGet();
            taskManager = new MetricCollector(this, thread);
        }

        taskMap.putIfAbsent(thread, taskManager);
        return taskManager.newRecord(taskName);

    }

    /**
     * 创建一个新的 Task任务, 并且启动
     *
     * @param taskName 任务名称
     */
    public RecordTask startRecord(String taskName) {
       return newRecord(taskName).start();
    }


    /**
     * 停止当前任务
     */
    public void stop() {

        if (getState() == NEW || getState() < 0) {
            return;
        }

        STATE.compareAndSet(this, STARTED, STOP);

        //等待所有任务完成
        waitComplete();

        shutdownNow();
    }


    public void shutdownNow() {
        for (; ; ) {
            int state = getState();
            if (getState() == STOP) {
                break;
            }

            //这里会有延迟机制，但是不会有太大影响
            if (STATE.compareAndSet(this, state, STOP)) {
                this.endTime = System.currentTimeMillis();
                //如果处于停止状态则直接唤醒阻塞的线程
                notifyThreads();
            }
        }
    }

    public boolean waitComplete() {
        for (MetricCollector collector : taskMap.values()) {
            collector.waitComplete();
            counter.decrementAndGet();
        }
        return true;
    }

    /**
     * 返回全部任务完成后的Future listener中执行，否则会导致数据采集不准确
     */
    public String pretty() {

        //如果当前是启动状态或者是正在停止状态，就要等待现阶段所有的任务执行完成
        if (isStarted() || isStopping()) {
            waitComplete();
        }

        return prettyNow();
    }

    /**
     * 不需要等待所有任务都执行完成，就可以打印当前记录仪的信息
     */
    public String prettyNow() {

        if (!isStop()) {
            this.endTime = System.currentTimeMillis();
        }

        StringBuilder builder = new StringBuilder();
        builder.append("All Records :[ ");

        //将所有的taskManger转为string
        taskMap.values().stream()
                .filter(manager -> manager.recordTasks.size() > 0)
                .forEach(taskManager -> builder.append("\n").append("" + taskManager.toString()));

        builder.append("\n], ExecuteTime is: ").append(getExecuteTime(startTime, endTime)).append("s");
        return builder.toString();
    }


    public boolean isStop() {
        return IsStop(getState());
    }


    public boolean isStopping() {
        return getState() == -1;
    }

    /**
     * 判断当前的记录器是否是启动状态
     */
    public boolean isStarted() {
        return IsStarted(getState());
    }


    /**
     * 唤醒所有的线程，只要任务执行完成，不管是否stop
     */
    private void notifyThreads() {
        try {
            lock.lock();
            completeCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void waitTask() {
        try {
            lock.lock();
            boolean interrupt = false;
            for (; ; ) {
                try {
                    completeCondition.await();
                    break;
                } catch (InterruptedException ex) {
                    interrupt = true;
                }
            }
            //如果对当前线程执行过interrupt操作，则在被唤醒后对当前线程进行中断
            if (interrupt)
                Thread.currentThread().interrupt();

        } finally {
            lock.unlock();
        }

    }

    public static double getExecuteTime(long startTime, long endTime) {
        return ((double) endTime - (double) startTime) / 1000;
    }


    public void incrementCompleteTask() {
        completeTasks.increment();
    }

    public long getCompleteTasks() {
        return completeTasks.sum();
    }

    /**
     * 当前record 是否没有被使用，是否存在完成的任务
     */
    public long hasCompleteTasks() {
        return completeTasks.sum();
    }

    public double executeTime() {
        return getExecuteTime(startTime, endTime);
    }


    /**
     * 用于帮助统计所有task的状态
     */
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class MetricCollector {

        /**
         * 保存当前节点中所有的任务
         */
        List<RecordTask> recordTasks;

        /**
         * 用于统计当前Bucket中所有的任务是否完成
         */
        long taskCounter;

        /**
         * 记录器
         */
        @Getter
        final ConcurrentTaskRecord record;

        /**
         * 和当前TaskManager绑定的线程
         */
        final Thread taskThread;

        /**
         * collector 开始时间
         */
        volatile long startTime;

        /**
         * 结束时间
         */
        volatile long endTime;

        long completeTasks;

        public MetricCollector(ConcurrentTaskRecord record, Thread taskThread) {
            this.recordTasks = new ArrayList<>();
            this.taskCounter = 0L;
            this.record = record;
            this.taskThread = taskThread;
            start();
        }

        public void start() {
            if (startTime == 0L) {
                startTime = System.currentTimeMillis();
            }
        }

        public void stop() {
            this.endTime = System.currentTimeMillis();
        }

        public RecordTask newRecord(String taskName) {
            assetSameThread();
            RecordTask recordTask = new RecordTask(this, taskName, false);
            taskCounter++;
            return recordTask;
        }

        private void complteTask() {
            taskCounter--;
        }

        public void repoter(RecordTask recordTask) {
            recordTasks.add(recordTask);
            taskCounter--;
            completeTasks++;
            record.incrementCompleteTask();

            if (taskCounter <= 0) {
                stop();
                record.notifyThreads();
            }
        }

        public void waitComplete() {

            for (; ; ) {
                if (taskCounter <= 0)
                    break;
                //等待所有任务都执行完成
                record.waitTask();
            }
        }

        boolean inSameThread() {
            return Thread.currentThread() == taskThread;
        }

        void assetSameThread() {
            //如果不在当前线程的话，则调用其他线程的TaskManager 进行处理
            if (!inSameThread()) {
                throw new UnsupportedOperationException("Unsupported annother thread operation this taskManager");
            }
        }

        double executeTime() {
            return getExecuteTime(startTime, endTime);
        }

        @Override
        public String toString() {

            //非结束状态的将不纳入统计
            List<RecordTask> taskInfos = recordTasks.stream()
                    .filter(recordTask -> recordTask.isStop() && !recordTask.isRecordState())
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            sb.append(" TaskManager: [")
                    .append(" complete task: ").append(completeTasks).append(",")
                    .append(" not complete: ").append(taskCounter).append(",")
                    .append(" thread: ").append(taskThread.getName())
                    .append("]")
                    .append("\n");

            sb.append("-----------------------------------------\n");
            sb.append("ms     %     Task name\n");
            sb.append("-----------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.ROOT);
            nf.setMinimumIntegerDigits(5);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance(Locale.ROOT);
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            for (RecordTask recordTask : taskInfos) {
                long taskTime = recordTask.getEndTime() - recordTask.getStartTime();
                sb.append(nf.format(taskTime)).append("  ");
                double frac = recordTask.executeTime() / executeTime();
                if (executeTime() == 0)
                    frac = 1;

                sb.append(pf.format(frac)).append("  ");
                sb.append(recordTask.getTaskName()).append("\n");
            }
            return sb.toString();
        }
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    public static class RecordTask implements Closeable {

        static final String EMPTY_TASK_NAME = RecordTask.class.getName() + "-" + "EMPTY";

        public static final RecordTask EMPTY = new RecordTask();
        /**
         * 执行的开始时间
         */
        long startTime = 0L;

        /**
         * 执行的结束时间
         */
        long endTime = 0L;

        /**
         * 当前task的状态
         */
        int state;

        /**
         * 任务名称
         */
        final String taskName;

        /**
         * 用于向task的管理者汇报状态
         */
        final MetricCollector collector;

        boolean recordState;

        private RecordTask() {
            this.recordState = true;
            this.collector = null;
            this.taskName = EMPTY_TASK_NAME;
        }

        public RecordTask(MetricCollector collector, String taskName, boolean recordState) {
            this.collector = collector;
            this.taskName = taskName;
            this.recordState = recordState;
//            start();
        }

        /**
         * 开始当前任务，允许多线程对同一个RecordTask 进行 start操作，
         * 如果非当前持有Record的线程对Record进行操作则会生成其他线程副本
         */
        public RecordTask start() {

            //如果记录器已经停止了，就不可以再对 RecordTask 做任务操作了
            if (recordState || isStop() || collector.getRecord().isStop()) {
                if (collector.getRecord() != null || collector.getRecord().isStop()) {
                    recordState = true;
                }
                return this;
            }

            collector.assetSameThread();

            if (!isStarted()) {
                this.state = STARTED;
                this.startTime = System.currentTimeMillis();
            }

            return this;
        }

        /**
         * 停止当前任务
         */
        public void stop() {
            collector.assetSameThread();

            //如果当前任务在执行的时候已经停止了
            if (recordState) {
                return;
            }

            if (getState() > 0) {
                this.state = STOP;
                this.endTime = System.currentTimeMillis();
                collector.repoter(this);
            }
        }

        public boolean isStop() {
            return IsStop(getState());
        }

        public boolean isStarted() {
            return IsStarted(getState());
        }

        public double executeTime() {
            return getExecuteTime(startTime, endTime);
        }

        /**
         * 可以使用try-with-resource 的方式来使用该recordTask
         */
        @Override
        public void close() throws IOException {
            stop();
        }

        @Override
        public String toString() {
            return StrUtil.format("RecordTask (taskName = {}, usedTime = {}s)", taskName, getExecuteTime(startTime, endTime));
        }
    }
}

