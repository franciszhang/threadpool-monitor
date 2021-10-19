package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.BaseResponse;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfo;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author francis
 * @version 2021-07-19
 */
@RestController
@RequestMapping("/threadpool/monitor")
public class MonitorManagerController {
    @Autowired
    private MachineService machineService;

    @RequestMapping("/register")
    public BaseResponse<?> register(MachineInfo request) {
        System.out.println("########" + JSONObject.toJSONString(request));
        machineService.register(request);
        return BaseResponse.isSuccess();
    }

    @GetMapping("/list")
    public BaseResponse<List<MachineInfo>> listExecutors() {
        return BaseResponse.isSuccess(machineService.list());
    }


}
