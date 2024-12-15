package com.maxqiu.demo.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.maxqiu.demo.entity.Demo;

/**
 * @author Max_Qiu
 */
@SpringBootTest
class DemoServiceTest {

    @Test
    void insert() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                Demo demo = new Demo();
                demo.insert();
                System.out.println(demo.getId());
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

}
