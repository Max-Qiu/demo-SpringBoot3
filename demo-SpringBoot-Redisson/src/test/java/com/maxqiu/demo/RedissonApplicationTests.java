package com.maxqiu.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class RedissonApplicationTests {

    private static Integer j = 0;

    @Resource
    private RedissonClient redissonClient;

    @Test
    void contextLoads() throws InterruptedException {
        long start = System.currentTimeMillis();
        // 一池N线程
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Callable<Void>> taskList = new ArrayList<>();
        // 启用N个线程
        for (int i = 1; i <= 1000; i++) {
            taskList.add(() -> {
                RLock lock = redissonClient.getLock("AAA");
                try {
                    if (lock.tryLock(10, 10, TimeUnit.MINUTES)) {
                        j++;
                        lock.unlock();
                    }
                } catch (Exception e) {
                    log.error("Error..", e);
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.debug("lock released");
                    }
                }
                return null;
            });
        }

        executorService.invokeAll(taskList);

        System.out.println(j);
        System.out.println(System.currentTimeMillis() - start);
    }

}
