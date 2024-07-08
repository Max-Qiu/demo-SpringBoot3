package com.maxqiu.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import jakarta.annotation.Resource;

/**
 * @author Max_Qiu
 */
@SpringBootTest
public class LongTest {

    @Resource
    private RedisTemplate<String, Long> longRedisTemplate;

    @Test
    void longTest() {
        ValueOperations<String, Long> longValueOperations = longRedisTemplate.opsForValue();
        // 测试写
        longValueOperations.set("test1", 1L);
        longValueOperations.set("test2", 1111111111111111111L);

        // 测试读
        Long test1 = longValueOperations.get("test1");
        System.out.println(test1);
        Long test2 = longValueOperations.get("test2");
        System.out.println(test2);
    }

}
