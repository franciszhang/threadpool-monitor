package com.frank.threadpool.monitor.dashboard.pojo;

import lombok.Data;

/**
 * @author francis
 * @version 2021-07-19
 */
@Data
public class MachineInfo {
    private String ip;
    private Integer port;
    private String hostname;
    private String appName;
    private Long timestamp;
}
