package com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.beat;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.beat.client.SimpleHttpClient;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.beat.client.SimpleHttpRequest;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.agent.util.PropertiesUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author francis
 * @version 2021-07-20
 */
public class HeartBeatHttpServer {
    private static final Map<String, String> heartbeatParams = new HashMap<>();
    private static final SimpleHttpClient simpleHttpClient = new SimpleHttpClient();

    static {
        heartbeatParams.put("hostname", PropertiesUtil.getHostName());
        heartbeatParams.put("ip", PropertiesUtil.getLocalIp());
        heartbeatParams.put("appName", PropertiesUtil.getAppName());
        heartbeatParams.put("port", PropertiesUtil.getManagePort());
        Timer t = new Timer();

    }

    public static void init() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    send();
                } catch (Exception e) {
                    System.out.println("heartBeat-exception:" + e.getMessage());
                }
            }
        }, 6000, PropertiesUtil.getHeartBeatInterval());
    }

    private static void send() {
        InetSocketAddress dashboardServerAddr = PropertiesUtil.getDashboardServerAddr();
        if (dashboardServerAddr == null) {
            System.out.println("dashboardServerAddr is null!");
            return;
        }
        SimpleHttpRequest request = new SimpleHttpRequest(dashboardServerAddr, "/threadpool/machine/register");
        heartbeatParams.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.setParams(heartbeatParams);
        try {
            simpleHttpClient.post(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
