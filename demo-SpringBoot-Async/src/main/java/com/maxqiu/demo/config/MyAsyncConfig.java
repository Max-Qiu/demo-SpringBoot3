package com.maxqiu.demo.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步配置
 *
 * 一定要添加 @EnableAsync 注解开启异步任务
 *
 * @author Max_Qiu
 */
@EnableAsync
@Configuration
public class MyAsyncConfig {
    @Bean(name = "myAsyncExecutor")
    public Executor myAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心池大小
        executor.setCorePoolSize(4);
        // 最大池大小
        executor.setMaxPoolSize(10);
        // 任务队列最大数量
        executor.setQueueCapacity(50);
        // 线程保持活动秒数
        executor.setKeepAliveSeconds(60);
        // 线程池名称前缀
        executor.setThreadNamePrefix("MyThreadNamePrefix_");
        // 当前线程池满了以后，处理新任务的策略（CallerRunsPolicy 表单使用调用者处理）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
}
