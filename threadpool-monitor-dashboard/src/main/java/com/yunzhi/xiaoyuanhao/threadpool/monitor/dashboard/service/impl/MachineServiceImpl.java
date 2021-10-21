package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.service.impl;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.MachineRepository;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.entity.MachineInfoEntity;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfo;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author francis
 * @version 2021-10-19
 */
@Service
public class MachineServiceImpl implements MachineService {
    @Autowired
    private MachineRepository machineRepository;

    @Override
    public void register(MachineInfo machineInfo) {
        machineRepository.save(machineInfo);
    }

    @Override
    public List<MachineInfo> list() {
        List<MachineInfoEntity> list = machineRepository.list();
        List<MachineInfo> machineInfos = new ArrayList<>();
        for (MachineInfoEntity entity : list) {
            MachineInfo machineInfo = new MachineInfo();
            machineInfo.setAppName(entity.getAppName());
            machineInfo.setHostname(entity.getHostname());
            machineInfo.setIp(entity.getIp());
            machineInfo.setPort(entity.getPort());
            machineInfo.setTimestamp(entity.getLastHeartbeat());
            machineInfos.add(machineInfo);
        }
        return machineInfos;
    }

    @Override
    public void remove(String ip) {
        machineRepository.remove(ip);
    }
}
