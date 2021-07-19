package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint;

import com.sun.net.httpserver.HttpServer;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint.pojo.ThreadPoolDataDTO;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.endpoint.pojo.ThreadPoolUpdateDTO;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.manager.ThreadPoolMonitorAgentManager;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.util.*;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author francis
 * @version 2021-07-14
 */
public class ThreadpoolEndpointServer {


    public static void initManageHttpServer() {
        Object managePort = System.getProperties().getOrDefault("manage.port", System.getenv("manage.port"));
        if (managePort == null || "".equals(managePort)) {
            managePort = 8081;
        }
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress((int) managePort), 0);
            server.createContext("/actuator/threadpool", httpExchange -> {
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(toJsonBytes(list()));
                os.close();
            });
            server.start();
            System.out.println("######threadpoolEndpoint-start-success#######");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Map<Integer, String> threadNameMap = new HashMap<>();
    private static final SystemMetrics systemMetrics = SystemMetrics.getSystemMetrics();

    private static List<ThreadPoolDataDTO> list() {
        String ip = PropertiesUtil.getLocalIp();
        String hostName = PropertiesUtil.getHostName();
        String appName = PropertiesUtil.getAppName();
        int systemCpuCount = systemMetrics.getSystemCpuCount();
        double systemLoadAverage1m = systemMetrics.getSystemLoadAverage1m();

        ArrayList<ThreadPoolDataDTO> list = new ArrayList<>();

        Collection<ThreadPoolExecutor> threadPoolExecutorCollection = ThreadPoolMonitorAgentManager.getThreadPoolExecutorCollection();
        for (ThreadPoolExecutor executor : threadPoolExecutorCollection) {
            ThreadPoolDataDTO convert = convert(executor);
            convert.setIp(ip);
            convert.setHost(hostName);
            convert.setAppName(appName);
            convert.setCpuCount(systemCpuCount);
            convert.setSystemLoadAverage1m(systemLoadAverage1m);
            list.add(convert);
        }
        return list;
    }

    private ThreadPoolUpdateDTO update(Integer hashCode, Integer coreSize, Integer maxSize) {
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

    private static ThreadPoolDataDTO convert(ThreadPoolExecutor executor) {
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
        return data;
    }

    private static String getThreadName(ThreadPoolExecutor threadPoolExecutor) {
        String threadName = threadNameMap.get(threadPoolExecutor.hashCode());
        if (threadName != null) {
            return threadName;
        }

        threadName = getThreadPrefixName(threadPoolExecutor);

        if (threadName == null || "".equals(threadName)) {
            threadName = getWorkerName(threadPoolExecutor);
        }

        if (threadName == null || "".equals(threadName)) {
            threadName = threadPoolExecutor.getThreadFactory().toString();
        }

        threadNameMap.put(threadPoolExecutor.hashCode(), threadName);
        return threadName;
    }

    private static String getThreadPrefixName(ThreadPoolExecutor threadPoolExecutor) {
        String threadPrefixName = null;
        ThreadFactory threadFactory = threadPoolExecutor.getThreadFactory();
        Class<?> aClass = threadFactory.getClass();

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object o = null;
            try {
                o = declaredField.get(threadFactory);
            } catch (Exception ignore) {
            }
            if (o instanceof String) {
                threadPrefixName = o.toString();
                break;
            }
        }
        return threadPrefixName;
    }

    private static String getWorkerName(ThreadPoolExecutor threadPoolExecutor) {
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
                return threadName;
            }
        } catch (Exception e) {
            // ignore
        }
        return threadName;
    }

    private static Field getWorkersField(ThreadPoolExecutor threadPoolExecutor) throws NoSuchFieldException {
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

    private static String dealThreadName(String name) {
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

    private static byte[] toJsonBytes(List<ThreadPoolDataDTO> list) {
        Object[] objects = list.toArray();
        String string = Arrays.toString(objects);
        return string.getBytes();
    }
}
