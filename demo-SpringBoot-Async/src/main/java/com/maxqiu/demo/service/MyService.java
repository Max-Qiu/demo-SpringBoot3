package com.maxqiu.demo.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Max_Qiu
 */
@Service
@Slf4j
public class MyService {
    /**
     * 无返回值
     */
    @Async("myAsyncExecutor")
    public void execute1(String arg) {
        log.info("当前线程名称：{}\t参数：{}", Thread.currentThread().getName(), arg);
    }

    /**
     * 有返回值
     */
    @Async("myAsyncExecutor")
    public CompletableFuture<Integer> execute2(Integer arg) {
        log.info("当前线程名称：{}\t参数：{}", Thread.currentThread().getName(), arg);
        return CompletableFuture.completedFuture(arg);
    }
}
