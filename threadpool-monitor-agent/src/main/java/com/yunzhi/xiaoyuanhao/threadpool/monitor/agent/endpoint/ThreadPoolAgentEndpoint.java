package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.manager.ThreadPoolMonitorAgentManager;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.util.PropertiesUtil;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author francis
 * @version 2021-06-07
 */
@WebEndpoint(id = "threadpool")
public class ThreadPoolAgentEndpoint {

    private final Map<Integer, String> threadNameMap = new HashMap<>();

    @ReadOperation(produces = "application/json; charset=UTF-8")
    public List<ThreadPoolData> list() {
        ArrayList<ThreadPoolData> list = new ArrayList<>();
        Collection<ThreadPoolExecutor> threadPoolExecutorCollection = ThreadPoolMonitorAgentManager.getThreadPoolExecutorCollection();
        for (ThreadPoolExecutor executor : threadPoolExecutorCollection) {
            list.add(convert(executor));
        }
        return list;
    }

    @WriteOperation(produces = "application/json; charset=UTF-8")
    public UpdateDTO update(Integer hashCode, Integer coreSize, Integer maxSize) {
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolMonitorAgentManager.getThreadPoolExecutor(hashCode);
        if (threadPoolExecutor == null) {
            return new UpdateDTO("未找到需要修改的线程");
        }
        if (maxSize == null || maxSize <= 0) {
            maxSize = threadPoolExecutor.getMaximumPoolSize();
        }
        if (coreSize == null || coreSize <= 0) {
            coreSize = threadPoolExecutor.getCorePoolSize();
        }
        if (maxSize < coreSize) {
            return new UpdateDTO("maxSize必须要大于coreSize");
        }
        // 修改
        threadPoolExecutor.setCorePoolSize(coreSize);
        threadPoolExecutor.setMaximumPoolSize(maxSize);
        return new UpdateDTO();
    }

    private ThreadPoolData convert(ThreadPoolExecutor executor) {
        ThreadPoolData data = new ThreadPoolData();
        data.setThreadPoolHashCode(executor.hashCode());
        data.setThreadPoolName(getThreadName(executor));
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
        data.setThreadPoolName(executor.getThreadFactory().toString());
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
                threadNameMap.put(threadPoolExecutor.hashCode(), threadName);
                return threadName;
            }
        } catch (Exception e) {
            // ignore
        }

        return "";
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

    public static class ThreadPoolData {
        private String threadPoolName;
        private int threadPoolHashCode;
        private int coreSize;
        private int maximumPoolSize;
        private int activeCount;

        private String queueType;
        private int queueCapacity;
        private int queueSize;
        private int queueRemainingSize;
        private long completedTaskCount;
        private long taskCount;
        private int largestPoolSize;
        private long rejectCount;
        private String rejectHandler;
        private String ip;
        private String host;
        private String appName;

        public String getThreadPoolName() {
            return threadPoolName;
        }

        public void setThreadPoolName(String threadPoolName) {
            this.threadPoolName = threadPoolName;
        }

        public int getThreadPoolHashCode() {
            return threadPoolHashCode;
        }

        public void setThreadPoolHashCode(int threadPoolHashCode) {
            this.threadPoolHashCode = threadPoolHashCode;
        }

        public int getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(int activeCount) {
            this.activeCount = activeCount;
        }

        public String getQueueType() {
            return queueType;
        }

        public void setQueueType(String queueType) {
            this.queueType = queueType;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }

        public int getQueueRemainingSize() {
            return queueRemainingSize;
        }

        public void setQueueRemainingSize(int queueRemainingSize) {
            this.queueRemainingSize = queueRemainingSize;
        }

        public long getCompletedTaskCount() {
            return completedTaskCount;
        }

        public void setCompletedTaskCount(long completedTaskCount) {
            this.completedTaskCount = completedTaskCount;
        }

        public int getLargestPoolSize() {
            return this.largestPoolSize;
        }

        public void setLargestPoolSize(int largestPoolSize) {
            this.largestPoolSize = largestPoolSize;
        }

        public long getRejectCount() {
            return rejectCount;
        }

        public void setRejectCount(long rejectCount) {
            this.rejectCount = rejectCount;
        }

        public String getRejectHandler() {
            return rejectHandler;
        }

        public void setRejectHandler(String rejectHandler) {
            this.rejectHandler = rejectHandler;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public long getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(long taskCount) {
            this.taskCount = taskCount;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }
    }

    private static class UpdateDTO {
        private Boolean success;
        private String errorMsg;

        public UpdateDTO(String errorMsg) {
            this.success = Boolean.FALSE;
            this.errorMsg = errorMsg;
        }

        public UpdateDTO() {
            this.success = Boolean.TRUE;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            success = success;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }
}
