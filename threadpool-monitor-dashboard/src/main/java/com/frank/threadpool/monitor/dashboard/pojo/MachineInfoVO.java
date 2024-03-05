package com.frank.threadpool.monitor.dashboard.pojo;

import lombok.Data;

/**
 * @author francis
 * @version 2021-10-20
 */
@Data
public class MachineInfoVO {
    private String ip;
    private Integer port;
    private String hostname;
    private String appName;
    private Integer status;
    private String lastTime;

}
