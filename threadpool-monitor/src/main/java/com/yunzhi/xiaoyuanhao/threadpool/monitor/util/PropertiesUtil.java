package com.yunzhi.xiaoyuanhao.threadpool.monitor.util;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * @author francis
 * @version 2021-05-26.
 */
public class PropertiesUtil {

    private static String localIp;
    private static String hostName;
    private static String appName;

    public static String getLocalIp() {
        return localIp;
    }

    public static String getHostName() {
        return hostName;
    }

    public static String getAppName() {
        return appName;
    }

    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            localIp = addr.getHostAddress();
            hostName = addr.getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        appName = System.getProperty("spring.application.name");
        if (StringUtils.isEmpty(appName)) {
            InputStream applicationInput = PropertiesUtil.class.getResourceAsStream("/application.properties");
            try {
                Properties properties = new Properties();
                properties.load(applicationInput);
                appName = properties.getOrDefault("spring.application.name", System.getProperty("project.name")).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
