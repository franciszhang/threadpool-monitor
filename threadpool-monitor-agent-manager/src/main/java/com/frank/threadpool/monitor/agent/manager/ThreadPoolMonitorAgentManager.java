package com.frank.threadpool.monitor.agent.manager;

import com.frank.threadpool.monitor.agent.beat.HeartBeatHttpServer;
import com.frank.threadpool.monitor.agent.endpoint.ThreadPoolEndpointHttpServer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author francis
 * @version 2021-06-07
 */
public class ThreadPoolMonitorAgentManager {

    static {
        ThreadPoolEndpointHttpServer.init();
        HeartBeatHttpServer.init();
    }

    private final static Map<Integer, ThreadPoolExecutor> THREAD_POOL_EXECUTOR_MAP = new ConcurrentHashMap<>();

    private final static Map<Integer, AtomicLong> REJECT_COUNT_MAP = new ConcurrentHashMap<>();

    public static void putThreadPoolExecutor(int obj, ThreadPoolExecutor threadPoolExecutor) {
        THREAD_POOL_EXECUTOR_MAP.put(obj, threadPoolExecutor);
        REJECT_COUNT_MAP.put(obj, new AtomicLong(0));
    }

    public static Collection<ThreadPoolExecutor> getThreadPoolExecutorCollection() {
        return THREAD_POOL_EXECUTOR_MAP.values();
    }


    public static ThreadPoolExecutor getThreadPoolExecutor(Integer obj) {
        return THREAD_POOL_EXECUTOR_MAP.get(obj);
    }


    public static AtomicLong getRejectCount(Integer obj) {
        return REJECT_COUNT_MAP.get(obj);
    }
}
