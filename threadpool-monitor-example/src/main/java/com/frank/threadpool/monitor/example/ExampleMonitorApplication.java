package com.frank.threadpool.monitor.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


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

    ThreadPoolExecutor defaultExecutor = new ThreadPoolExecutor(10,100,60L, TimeUnit.SECONDS,new LinkedBlockingDeque<>(1000));
    ThreadPoolExecutor defaultExecutor1 = new ThreadPoolExecutor(20,200,60L, TimeUnit.SECONDS,new LinkedBlockingDeque<>(1000));
    ThreadPoolExecutor defaultExecutor2 = new ThreadPoolExecutor(30,300,60L, TimeUnit.SECONDS,new LinkedBlockingDeque<>(1000));
    ThreadPoolExecutor defaultExecutor3 = new ThreadPoolExecutor(40,400,60L, TimeUnit.SECONDS,new LinkedBlockingDeque<>(1000));

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
