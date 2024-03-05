package com.frank.threadpool.monitor.dashboard.controller;

import com.alibaba.fastjson.JSONObject;
import com.frank.threadpool.monitor.dashboard.pojo.BaseResponse;
import com.frank.threadpool.monitor.dashboard.pojo.MachineInfo;
import com.frank.threadpool.monitor.dashboard.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author francis
 * @version 2021-07-19
 */
@RestController
@RequestMapping("/threadpool/machine")
public class MachineManagerController {
    @Autowired
    private MachineService machineService;

    @RequestMapping("/register")
    public BaseResponse<?> register(MachineInfo request) {
        System.out.println("########" + JSONObject.toJSONString(request));
        machineService.register(request);
        return BaseResponse.isSuccess();
    }

    @GetMapping("/list")
    public BaseResponse<List<MachineInfo>> listMachine() {
        return BaseResponse.isSuccess(machineService.list());
    }

    @PostMapping("/remove")
    public BaseResponse<Void> removeMachine(String ip) {
        machineService.remove(ip);
        return BaseResponse.isSuccess();
    }

}
