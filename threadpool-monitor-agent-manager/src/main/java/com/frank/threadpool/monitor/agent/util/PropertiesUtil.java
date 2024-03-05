package com.frank.threadpool.monitor.agent.util;


import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @author francis
 * @version 2021-05-26.
 */
public class PropertiesUtil {

    private static String localIp;
    private static String hostName;
    private static String appName;
    private static String dashboardServer;
    private static String managePort = "8081";
    private static InetSocketAddress addr;
    //心跳间隔，默认1分钟
    private static Long heartBeatInterval = 60000L;

    public static String getLocalIp() {
        return localIp;
    }

    public static String getHostName() {
        return hostName;
    }

    public static String getAppName() {
        return appName;
    }

    public static InetSocketAddress getDashboardServerAddr() {
        return getDashboardAddr();
    }

    public static String getManagePort() {
        return managePort;
    }

    public static Long getHeartBeatInterval() {
        return heartBeatInterval;
    }

    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            if (addr != null) {
                localIp = addr.getHostAddress();
                hostName = addr.getHostName();
            }
        } catch (Exception e) {
            throw new RuntimeException("get local ip error", e);
        }

        Properties properties = new Properties();
        try {
            InputStream applicationInput = PropertiesUtil.class.getResourceAsStream("/application.properties");
            if (applicationInput != null) {
                properties.load(applicationInput);
            }
        } catch (Exception e) {
            throw new RuntimeException("load application.properties error", e);
        }
        appName = System.getProperty("spring.application.name");
        if (appName == null) {
            appName = properties.getOrDefault("spring.application.name", System.getProperty("project.name")).toString();
        }
        dashboardServer = System.getProperty("threadpool.dashboard.server");
        if (dashboardServer == null) {
            Object dashboardServerObj = properties.get("threadpool.dashboard.server");
            if (dashboardServerObj == null) {
                dashboardServer = System.getenv("threadpool.dashboard.server");
            } else {
                dashboardServer = dashboardServerObj.toString();
            }
        }
        String intervalStr = System.getProperty("threadpool.heartbeat.interval", System.getenv("threadpool.heartbeat.interval"));
        if (intervalStr != null && !intervalStr.isEmpty()) {
            heartBeatInterval = Long.valueOf(intervalStr);
        }
        Object managePortObject = System.getProperty("manage.port", System.getenv("manage.port"));
        if (managePortObject != null && !"".equals(managePortObject)) {
            managePort = managePortObject.toString();
        }

    }

    private static InetSocketAddress getDashboardAddr() {
        if (addr != null) {
            return addr;
        }
        try {
            if (dashboardServer == null) {
                System.out.println("[HeartbeatHttpServer] dashboard server address not configured");
                return null;
            }
            if (dashboardServer.trim().isEmpty()) {
                return null;
            }
            String[] ipPort = dashboardServer.trim().split(":");
            int port = 80;
            if (ipPort.length > 1) {
                port = Integer.parseInt(ipPort[1].trim());
            }
            addr = new InetSocketAddress(ipPort[0].trim(), port);
            return addr;
        } catch (Exception ex) {
            System.out.println("[HeartbeatHttpServer] Parse dashboard list failed, " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
