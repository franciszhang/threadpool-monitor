package com.frank.threadpool.monitor.agent.interceptor;

import com.frank.threadpool.monitor.agent.manager.ThreadPoolMonitorAgentManager;
import net.bytebuddy.asm.Advice;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author francis
 * @version 2021-06-07
 */
public class ThreadPoolRejectInterceptor {
    @Advice.OnMethodEnter
    public static void intercept(@Advice.This Object obj) {
        AtomicLong rejectCount = ThreadPoolMonitorAgentManager.getRejectCount(System.identityHashCode(obj));
        if (rejectCount != null) {
            rejectCount.incrementAndGet();
        }

    }
}
