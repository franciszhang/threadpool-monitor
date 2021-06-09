package com.yunzhi.xiaoyuanhao.threadpool.monitor;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.manager.ThreadPoolMonitorAgentManager;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.pojo.ThreadPoolDataDTO;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.pojo.ThreadPoolUpdateDTO;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.util.PropertiesUtil;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author francis
 * @version 2021-06-07
 */
@Component
@WebEndpoint(id = "threadpool")
public class ThreadPoolAgentEndpoint {
    private final Map<Integer, String> threadNameMap = new HashMap<>();

    @ReadOperation(produces = "application/json; charset=UTF-8")
    public List<ThreadPoolDataDTO> list() {
        ArrayList<ThreadPoolDataDTO> list = new ArrayList<>();
        Collection<ThreadPoolExecutor> threadPoolExecutorCollection = ThreadPoolMonitorAgentManager.getThreadPoolExecutorCollection();
        for (ThreadPoolExecutor executor : threadPoolExecutorCollection) {
            list.add(convert(executor));
        }
        return list;
    }

    @WriteOperation(produces = "application/json; charset=UTF-8")
    public ThreadPoolUpdateDTO update(Integer hashCode, Integer coreSize, Integer maxSize) {
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolMonitorAgentManager.getThreadPoolExecutor(hashCode);
        if (threadPoolExecutor == null) {
            return new ThreadPoolUpdateDTO("未找到需要修改的线程");
        }
        if (maxSize == null || maxSize <= 0) {
            maxSize = threadPoolExecutor.getMaximumPoolSize();
        }
        if (coreSize == null || coreSize <= 0) {
            coreSize = threadPoolExecutor.getCorePoolSize();
        }
        if (maxSize < coreSize) {
            return new ThreadPoolUpdateDTO("maxSize必须要大于coreSize");
        }
        // 修改
        threadPoolExecutor.setCorePoolSize(coreSize);
        threadPoolExecutor.setMaximumPoolSize(maxSize);
        return new ThreadPoolUpdateDTO();
    }

    private ThreadPoolDataDTO convert(ThreadPoolExecutor executor) {
        ThreadPoolDataDTO data = new ThreadPoolDataDTO();
        data.setThreadPoolHashCode(executor.hashCode());
        data.setThreadPoolName(getThreadName(executor));
        data.setThreadPoolFactoryClass(executor.getThreadFactory().getClass().getName());
        data.setCoreSize(executor.getCorePoolSize());
        data.setMaximumPoolSize(executor.getMaximumPoolSize());
        data.setActiveCount(executor.getActiveCount());
        data.setCompletedTaskCount(executor.getCompletedTaskCount());
        data.setLargestPoolSize(executor.getLargestPoolSize());
        data.setTaskCount(executor.getTaskCount());
        BlockingQueue<Runnable> queue = executor.getQueue();
        data.setQueueType(queue.getClass().getSimpleName());
        data.setQueueCapacity(queue.remainingCapacity() == Integer.MAX_VALUE ? Integer.MAX_VALUE : queue.remainingCapacity() + queue.size());
        data.setQueueSize(queue.size());
        data.setQueueRemainingSize(queue.remainingCapacity());
        data.setRejectCount(ThreadPoolMonitorAgentManager.getRejectCount(executor.hashCode()).intValue());
        data.setRejectHandler(executor.getRejectedExecutionHandler().getClass().getSimpleName());
        data.setIp(PropertiesUtil.getLocalIp());
        data.setHost(PropertiesUtil.getHostName());
        data.setAppName(PropertiesUtil.getAppName());
        return data;
    }

    private String getThreadName(ThreadPoolExecutor threadPoolExecutor) {
        String threadName = threadNameMap.get(threadPoolExecutor.hashCode());
        if (threadName != null) {
            return threadName;
        }

        threadName = getThreadPrefixName(threadPoolExecutor);

        if (!StringUtils.hasText(threadName)) {
            threadName = getWorkerName(threadPoolExecutor);
        }

        if (!StringUtils.hasText(threadName)) {
            threadName = threadPoolExecutor.getThreadFactory().toString();
        }

        threadNameMap.put(threadPoolExecutor.hashCode(), threadName);
        return threadName;
    }

    private String getThreadPrefixName(ThreadPoolExecutor threadPoolExecutor) {
        String threadPrefixName = null;
        ThreadFactory threadFactory = threadPoolExecutor.getThreadFactory();
        Class<?> aClass = threadFactory.getClass();
        try {
            Field namePrefixField = aClass.getDeclaredField("namePrefix");
            namePrefixField.setAccessible(true);
            threadPrefixName = (String) namePrefixField.get(threadFactory);
        } catch (Exception ignore) {
        }
        return threadPrefixName;
    }

    private String getWorkerName(ThreadPoolExecutor threadPoolExecutor) {
        String threadName = null;
        try {
            Field workersField = getWorkersField(threadPoolExecutor);
            workersField.setAccessible(true);
            HashSet<?> workers = (HashSet<?>) workersField.get(threadPoolExecutor);
            Iterator<?> iterator = workers.iterator();

            if (iterator.hasNext()) {
                Object next = iterator.next();
                Field threadField = next.getClass().getDeclaredField("thread");
                threadField.setAccessible(true);
                Thread thread = (Thread) threadField.get(next);
                threadName = dealThreadName(thread.getName());
                if (StringUtils.hasText(threadName)) {
                    return threadName;
                } else {
                    return threadPoolExecutor.getThreadFactory().toString();
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return threadName;
    }

    private Field getWorkersField(ThreadPoolExecutor threadPoolExecutor) throws NoSuchFieldException {
        // must three times
        Class<?> aClass = threadPoolExecutor.getClass();
        for (int i = 0; i < 3; i++) {
            try {
                return aClass.getDeclaredField("workers");
            } catch (Exception e) {
                aClass = aClass.getSuperclass();
                if (aClass == null) {
                    throw e;
                }
            }

        }
        throw new NoSuchFieldException();
    }

    private String dealThreadName(String name) {
        String[] split = name.split("-");
        if (split.length == 1) {
            return name;
        }
        String suffix = split[split.length - 1];

        try {
            Integer.parseInt(suffix);

            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < split.length - 1; i++) {
                ret.append(split[i]);
                if (i < split.length - 2) {
                    ret.append("-");
                }
            }
            return ret.toString();
        } catch (Exception e) {
            // ignore
        }

        return name;
    }
}
