package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.service;


import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfo;

import java.util.List;

/**
 * @author francis
 * @version 2021-10-19
 */
public interface MachineService {

    void register(MachineInfo machineInfo);

    List<MachineInfo> list();
}
