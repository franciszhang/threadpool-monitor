package com.yunzhi.xiaoyuanhao.example.monitor;

import com.yunzhi.xiaoyuanhao.threadpool.monitor.YzThreadPoolExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
public class ExampleMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleMonitorApplication.class, args);
    }


    @RequestMapping("/init")
    public String initPool(@RequestParam(defaultValue = "10") Integer num) {
        init(num);
        return "success";
    }

    YzThreadPoolExecutor defaultExecutor = YzThreadPoolExecutor.getDefaultExecutor("test-pool");
    YzThreadPoolExecutor defaultExecutor1 = YzThreadPoolExecutor.getDefaultExecutor("test-pool");
    YzThreadPoolExecutor defaultExecutor2 = YzThreadPoolExecutor.getDefaultExecutor("test-pool");
    YzThreadPoolExecutor defaultExecutor3 = YzThreadPoolExecutor.getDefaultExecutor("test-pool");

    private void init(int num) {

        for (int i = 0; i < num; i++) {
            defaultExecutor.execute(() -> {
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("defaultExecutor:" + Thread.currentThread().getName());
            });
        }
        num = num - 100;
        for (int i = 0; i < num; i++) {
            defaultExecutor1.execute(() -> {
                try {
                    Thread.sleep(15000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("defaultExecutor1:" + Thread.currentThread().getName());
            });
        }
        num = num - 100;

        for (int i = 0; i < num; i++) {
            defaultExecutor2.execute(() -> {
                try {
                    Thread.sleep(20000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("defaultExecutor2:" + Thread.currentThread().getName());
            });
        }

        num = num - 100;
        for (int i = 0; i < num; i++) {
            defaultExecutor3.execute(() -> {
                try {
                    Thread.sleep(25000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("defaultExecutor3:" + Thread.currentThread().getName());
            });
        }

    }
}
