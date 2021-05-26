package com.yunzhi.xiaoyuanhao.threadpool.monitor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author francis
 * @version 2021-05-26
 */
public abstract class YzRejectedExecutionHandler implements RejectedExecutionHandler {
    private final AtomicLong rejectCount;

    public YzRejectedExecutionHandler() {
        rejectCount = new AtomicLong(0);
    }

    private void incrementCount() {
        getRejectCount().incrementAndGet();
    }


    protected abstract void yzRejectedExecution(Runnable r, YzThreadPoolExecutor executor);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        incrementCount();
        yzRejectedExecution(r, (YzThreadPoolExecutor) executor);
    }

    public AtomicLong getRejectCount() {
        return rejectCount;
    }
}
