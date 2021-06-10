package com.yunzhi.xiaoyuanhao.threadpool.monitor.sys;

import io.micrometer.core.lang.Nullable;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author francis
 * @version 2021-06-09
 */
public class SystemMetrics {
    private static SystemMetrics systemMetrics;
    private static final List<String> OPERATING_SYSTEM_BEAN_CLASS_NAMES = Arrays.asList("com.sun.management.OperatingSystemMXBean", "com.ibm.lang.management.OperatingSystemMXBean");
    private final OperatingSystemMXBean operatingSystemBean;
    private final Class<?> operatingSystemBeanClass;
    private final Method systemCpuUsage;
    private final Method processCpuUsage;


    public static synchronized SystemMetrics getSystemMetrics() {
        if (systemMetrics == null) {
            systemMetrics = new SystemMetrics();
        }
        return systemMetrics;
    }

    public int getSystemCpuCount() {
        return operatingSystemBean.getAvailableProcessors();
    }

    public double getSystemLoadAverage1m() {
        return operatingSystemBean.getSystemLoadAverage();
    }

    public double getSystempuUsage() {
        return this.invoke(this.systemCpuUsage);
    }

    public double getProcessCpuUsage() {
        return this.invoke(this.processCpuUsage);
    }

    private SystemMetrics() {
        this.operatingSystemBean = ManagementFactory.getOperatingSystemMXBean();
        this.operatingSystemBeanClass = this.getFirstClassFound(OPERATING_SYSTEM_BEAN_CLASS_NAMES);
        this.systemCpuUsage = this.detectMethod("getSystemCpuLoad");
        this.processCpuUsage = this.detectMethod("getProcessCpuLoad");
    }

    private Method detectMethod(String name) {
        Objects.requireNonNull(name);
        if (this.operatingSystemBeanClass == null) {
            return null;
        } else {
            try {
                this.operatingSystemBeanClass.cast(this.operatingSystemBean);
                return this.operatingSystemBeanClass.getDeclaredMethod(name);
            } catch (NoSuchMethodException | SecurityException | ClassCastException var3) {
                return null;
            }
        }
    }

    private Class<?> getFirstClassFound(List<String> classNames) {
        Iterator var2 = classNames.iterator();

        while (var2.hasNext()) {
            String className = (String) var2.next();

            try {
                return Class.forName(className);
            } catch (ClassNotFoundException var5) {
            }
        }
        return null;
    }

    private double invoke(@Nullable Method method) {
        try {
            return method != null ? (Double) method.invoke(this.operatingSystemBean) : 0.0D / 0.0;
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
            return 0.0D / 0.0;
        }
    }
}
