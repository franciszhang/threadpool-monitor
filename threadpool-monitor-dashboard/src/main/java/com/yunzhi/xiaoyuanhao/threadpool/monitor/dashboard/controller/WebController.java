package com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfo;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.MachineInfoVO;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.pojo.ThreadpoolVO;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.service.MachineService;
import com.yunzhi.xiaoyuanhao.threadpool.monitor.dashboard.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author francis
 * @version 2021-10-19
 */
@Slf4j
@Controller
public class WebController {
    @Autowired
    private MachineService machineService;

    @RequestMapping({"/index", "/"})
    public String index(ModelMap modelMap) {
        List<MachineInfo> list = machineService.list();
        List<MachineInfoVO> machineInfoList = convertVo(list);
        modelMap.put("list", machineInfoList);
        return "index";
    }

    @RequestMapping("/threadpool/list")
    public String threadPoolList(ModelMap modelMap, String ip, String port) {
        String s = HttpUtils.get("http://" + ip + ":" + port + "/actuator/threadpool");
        if (!StringUtils.hasText(s)) {
            return "error";
        }
        List<ThreadpoolVO> list = JSONArray.parseArray(s, ThreadpoolVO.class);
        list.sort(Comparator.comparing(ThreadpoolVO::getThreadPoolName));
        for (ThreadpoolVO threadpoolVO : list) {
            threadpoolVO.setPort(port);
        }
        modelMap.put("list", list);
        return "threadpoolList";
    }

    @GetMapping("/update/threadpool")
    public String updateThreadPool(ModelMap modelMap, String ip, String port,
                                   String hashCode, String coreSize,
                                   String maxSize) {
        modelMap.put("ip", ip);
        modelMap.put("port", port);
        modelMap.put("hashCode", hashCode);
        modelMap.put("coreSize", coreSize);
        modelMap.put("maxSize", maxSize);
        return "updateThreadpool";
    }

    @PostMapping("/update/threadpool")
    @ResponseBody
    public String updateThreadPool(@RequestBody JSONObject jsonObject) {
        String hashCode = jsonObject.getString("hashCode");
        String coreSize = jsonObject.getString("coreSize");
        String maxSize = jsonObject.getString("maxSize");
        String ip = jsonObject.getString("ip");
        String port = jsonObject.getString("port");
        if (Strings.isBlank(ip) || Strings.isBlank(port) || Strings.isBlank(hashCode) || Strings.isBlank(coreSize) || Strings.isBlank(maxSize)) {
            log.error("updateThreadPool-param:[{}]", jsonObject);
            return "参数错误";
        }
        String result = HttpUtils.post("http://" + ip + ":" + port + "/actuator/threadpool", jsonObject.toJSONString());
        log.info("updateThreadpool-response:[{}]", result);
        return result;
    }


    public List<MachineInfoVO> convertVo(List<MachineInfo> list) {
        ArrayList<MachineInfoVO> machineInfoList = new ArrayList<>();
        long l = System.currentTimeMillis();
        for (MachineInfo machineInfo : list) {
            MachineInfoVO machineInfoVO = new MachineInfoVO();
            BeanUtils.copyProperties(machineInfo, machineInfoVO);
            machineInfoVO.setStatus(1);
            if (l - machineInfo.getTimestamp() > 3 * 60 * 1000) {
                machineInfoVO.setStatus(0);
            }
            machineInfoVO.setLastTime(longTimeToStringTime(machineInfo.getTimestamp()));
            machineInfoList.add(machineInfoVO);
        }
        return machineInfoList;
    }

    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String longTimeToStringTime(Long timestamp) {
        Date date = new Date(timestamp);
        return DF.format(date);

    }
}
