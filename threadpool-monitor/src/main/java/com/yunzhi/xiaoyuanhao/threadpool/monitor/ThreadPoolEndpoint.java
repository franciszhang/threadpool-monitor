package com.yunzhi.xiaoyuanhao.threadpool.monitor;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.pojo.ThreadPoolData;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.util.PropertiesUtil;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;


/**
 * @author francis
 * @version 2021-05-25
 */
@Component
@Endpoint(id = "threadpool")
public class ThreadPoolEndpoint {

    @ReadOperation(produces = "application/json; charset=UTF-8")
    public List<ThreadPoolData> list() {
        ArrayList<ThreadPoolData> list = new ArrayList<>();
        Map<String, YzThreadPoolExecutor> threadPoolMap = ThreadPoolMonitorManager.getThreadPoolMap();
        for (Map.Entry<String, YzThreadPoolExecutor> executorEntry : threadPoolMap.entrySet()) {
            String key = executorEntry.getKey();
            YzThreadPoolExecutor value = executorEntry.getValue();
            ThreadPoolData data = convert(value);
            data.setThreadPoolName(key);
            list.add(data);
        }
        return list;
    }

    private ThreadPoolData convert(YzThreadPoolExecutor executor) {
        ThreadPoolData data = new ThreadPoolData();
        data.setCoreSize(executor.getCorePoolSize());
        data.setMaximumPoolSize(executor.getMaximumPoolSize());
        data.setActiveCount(executor.getActiveCount());
        data.setCompletedTaskCount(executor.getCompletedTaskCount());
        data.setLargestPoolSize(executor.getLargestPoolSize());
        data.setTaskCount(executor.getTaskCount());
        BlockingQueue<Runnable> queue = executor.getQueue();
        data.setQueueType(queue.getClass().getSimpleName());
        data.setQueueCapacity(queue.remainingCapacity() + queue.size());
        data.setQueueSize(queue.size());
        data.setQueueRemainingSize(queue.remainingCapacity());
        RejectedExecutionHandler rejectedExecutionHandler = executor.getRejectedExecutionHandler();
        if (rejectedExecutionHandler instanceof YzRejectedExecutionHandler) {
            data.setRejectCount(((YzRejectedExecutionHandler) rejectedExecutionHandler).getRejectCount().get());
        }
        data.setRejectHandler(executor.getRejectedExecutionHandler().getClass().getSimpleName());
        data.setThreadPoolName(executor.getThreadFactory().toString());
        data.setIp(PropertiesUtil.getLocalIp());
        data.setHost(PropertiesUtil.getHostName());
        data.setAppName(PropertiesUtil.getAppName());
        return data;
    }


}
