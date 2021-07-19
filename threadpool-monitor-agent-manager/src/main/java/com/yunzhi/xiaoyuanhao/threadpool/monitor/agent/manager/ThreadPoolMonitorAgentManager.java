package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.manager;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint.ThreadpoolEndpointServer;

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
        ThreadpoolEndpointServer.initManageHttpServer();
    }

    private final static Map<Integer, ThreadPoolExecutor> THREAD_POOL_EXECUTOR_MAP = new ConcurrentHashMap<>();

    private final static Map<Integer, AtomicLong> REJECT_COUNT_MAP = new ConcurrentHashMap<>();

    public static void putThreadPoolExecutor(Integer hashCode, ThreadPoolExecutor threadPoolExecutor) {
        THREAD_POOL_EXECUTOR_MAP.put(hashCode, threadPoolExecutor);
        REJECT_COUNT_MAP.put(hashCode, new AtomicLong(0));
    }

    public static Collection<ThreadPoolExecutor> getThreadPoolExecutorCollection() {
        return THREAD_POOL_EXECUTOR_MAP.values();
    }


    public static ThreadPoolExecutor getThreadPoolExecutor(Integer hashCode) {
        return THREAD_POOL_EXECUTOR_MAP.get(hashCode);
    }


    public static AtomicLong getRejectCount(Integer hashCode) {
        return REJECT_COUNT_MAP.get(hashCode);
    }
}
