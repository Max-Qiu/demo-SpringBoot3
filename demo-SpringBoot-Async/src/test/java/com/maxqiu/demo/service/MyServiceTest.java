package com.maxqiu.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

/**
 * @author Max_Qiu
 */
@SpringBootTest
class MyServiceTest {
    @Resource
    private MyService myService;

    /**
     * 无返回值
     */
    @Test
    void execute1() {
        myService.execute1("参数");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程结束");
    }

    /**
     * 有返回值，固定任务数量
     */
    @Test
    void execute2() {
        // 用于存储返回结果的 list 必须使用线程安全的对象，因为会并发操作
        List<Integer> resultList = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<Integer> e1 = myService.execute2(1).whenComplete((r, e) -> resultList.add(r));
        CompletableFuture<Integer> e2 = myService.execute2(2).whenComplete((r, e) -> resultList.add(r));
        CompletableFuture<Integer> e3 = myService.execute2(3).whenComplete((r, e) -> resultList.add(r));

        CompletableFuture.allOf(e1, e2, e3).join();

        System.out.println(resultList);
        System.out.println("主线程结束");
    }

    /**
     * 有返回值，不定任务数量
     */
    @Test
    void execute3() {
        int jobSize = 10;

        // 用于存储返回结果的 list 必须使用线程安全的对象，因为会并发操作
        List<Integer> resultList = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Integer>> completableFutureList = new ArrayList<>();
        for (int i = 0; i < jobSize; i++) {
            completableFutureList.add(myService.execute2(i).whenComplete((r, e) -> resultList.add(r)));
        }
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[jobSize])).join();

        System.out.println(resultList);
        System.out.println("主线程结束");
    }
}
