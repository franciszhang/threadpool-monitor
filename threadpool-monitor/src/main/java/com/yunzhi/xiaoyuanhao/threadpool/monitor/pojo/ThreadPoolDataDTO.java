package com.yunzhi.xiaoyuanhao.threadpool.monitor.pojo;

/**
 * @author francis
 * @version 2021-05-25
 */
public class ThreadPoolDataDTO {
    private String threadPoolName;
    private String threadPoolFactoryClass;
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

    public String getThreadPoolFactoryClass() {
        return threadPoolFactoryClass;
    }

    public void setThreadPoolFactoryClass(String threadPoolFactoryClass) {
        this.threadPoolFactoryClass = threadPoolFactoryClass;
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
