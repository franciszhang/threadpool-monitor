package com.yunzhi.xiaoyuanhao.threadpool.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author francis
 * @version 2021-05-25
 */
class ThreadPoolMonitorManager {
    private static int i = 0;
    private static final Map<String, YzThreadPoolExecutor> name2threadPoolMap = new ConcurrentHashMap<>();

    static String put(String name, YzThreadPoolExecutor executor) {
        if (name2threadPoolMap.containsKey(name)) {
            i++;
            name = name + i;
        }
        name2threadPoolMap.put(name, executor);
        return name;
    }

    static Map<String, YzThreadPoolExecutor> getThreadPoolMap() {
        return name2threadPoolMap;
    }

    public static YzThreadPoolExecutor getThreadPoolMap(String poolName) {
        return name2threadPoolMap.get(poolName);
    }
}
