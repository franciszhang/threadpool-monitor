package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.impl;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.ThreadpoolRepository;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.dao.entity.MachineInfoEntity;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author francis
 * @version 2021-10-19
 */
@Service
public class MemoryThreadpoolRepositoryImpl implements ThreadpoolRepository {
    private static Map<String, MachineInfoEntity> ip2machineMap = new ConcurrentHashMap<>();

    @Override
    public void save(MachineInfo machineInfo) {
        MachineInfoEntity machineInfoEntity = ip2machineMap.get(machineInfo.getIp());
        if (machineInfoEntity == null) {
            machineInfoEntity = new MachineInfoEntity();
        }
        machineInfoEntity.setAppName(machineInfo.getAppName());
        machineInfoEntity.setHostname(machineInfo.getHostname());
        machineInfoEntity.setIp(machineInfo.getIp());
        machineInfoEntity.setPort(machineInfo.getPort());
        machineInfoEntity.setLastHeartbeat(machineInfo.getTimestamp());
        ip2machineMap.put(machineInfo.getIp(), machineInfoEntity);
    }

    @Override
    public List<MachineInfoEntity> list() {
        return new ArrayList<>(ip2machineMap.values());
    }
}
