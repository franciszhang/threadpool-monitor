package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.entity.MachineInfoEntity;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfo;

import java.util.List;

/**
 * @author francis
 * @version 2021-10-19
 */
public interface MachineRepository {

    void save(MachineInfo registerRequest);

    List<MachineInfoEntity> list();

    void remove(String ip);
}
