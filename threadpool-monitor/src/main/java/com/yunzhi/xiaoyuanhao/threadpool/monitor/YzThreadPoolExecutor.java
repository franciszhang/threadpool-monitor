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
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new DefaultYzRejectedExecutionHandler(), poolName);
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


    public static class DefaultYzRejectedExecutionHandler extends YzRejectedExecutionHandler {
        @Override
        protected void yzRejectedExecution(Runnable r, YzThreadPoolExecutor executor) {
            System.out.println("threadName:" + Thread.currentThread().getName() + ",yzRejectedExecution executor:" + executor.getPoolName() + ",reject" + getRejectCount().get());

        }
    }

}
