package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.entity;

import lombok.Data;

/**
 * @author francis
 * @version 2021-10-19
 */
@Data
public class MachineInfoEntity {
    private String ip;
    private Integer port;
    private String hostname;
    private String appName;
    private Long lastHeartbeat;
}
