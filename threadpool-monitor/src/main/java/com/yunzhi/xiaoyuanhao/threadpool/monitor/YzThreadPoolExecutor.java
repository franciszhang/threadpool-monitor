package com.yunzhi.xiaoyuanhao.threadpool.monitor;

import java.util.concurrent.*;

/**
 * @author francis
 * @version 2021-05-25
 */
public class YzThreadPoolExecutor extends ThreadPoolExecutor {
    private final String poolName;

    public String getPoolName() {
        return poolName;
    }

    public static YzThreadPoolExecutor getDefaultExecutor(String poolName) {
        return new YzThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100), Executors.defaultThreadFactory(), poolName);
    }

    public YzThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String poolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(poolName), poolName);
    }

    public YzThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String poolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new DiscardPolicy(), poolName);
    }

    public YzThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, YzRejectedExecutionHandler handler, String poolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler, poolName);
    }

    public YzThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, YzRejectedExecutionHandler handler, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.poolName = ThreadPoolMonitorManager.put(poolName, this);
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        super.terminated();
    }


    public static class CallerRunsPolicy extends YzRejectedExecutionHandler {
        @Override
        protected void rejectedExecution(Runnable r, YzThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                r.run();
            }
        }

    }

    public static class AbortPolicy extends YzRejectedExecutionHandler {
        @Override
        protected void rejectedExecution(Runnable r, YzThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " +
                    e.toString());
        }
    }

    public static class DiscardPolicy extends YzRejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, YzThreadPoolExecutor e) {
        }
    }

    public static class DiscardOldestPolicy extends YzRejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, YzThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }


}
