package com.maxqiu.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.maxqiu.demo.request.UserRequest;

import jakarta.annotation.Resource;

/**
 * 测试缓存名称生成规则
 *
 * @author Max_Qiu
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheKeyServiceTest {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 仅用于每个测试用例前清空数据库
     */
    @BeforeEach
    void clean() {
        Set<String> keys = stringRedisTemplate.keys("*");
        if (keys != null && keys.size() != 0) {
            // 清空缓存
            stringRedisTemplate.delete(keys);
        }
    }

    @Autowired
    private CacheKeyService service;

    @Test
    @Order(1)
    void normal() {
        System.out.println(service.normal());
        String s = stringRedisTemplate.opsForValue().get("cacheName::SimpleKey []");
        assertEquals("\"normal\"", s);
    }

    @Test
    @Order(2)
    void noParameter() {
        System.out.println(service.noParameter());
        String s = stringRedisTemplate.opsForValue().get("myCacheName::SimpleKey []");
        assertEquals("\"noParameter\"", s);
    }

    @Test
    @Order(3)
    void singleParameter() {
        System.out.println(service.singleParameter(1));
        String s = stringRedisTemplate.opsForValue().get("myCacheName::1");
        assertEquals("1", s);
    }

    @Test
    @Order(4)
    void requestParameter() {
        System.out.println(service.requestParameter(new UserRequest(1, "张三")));
        String s = stringRedisTemplate.opsForValue().get("myCacheName::UserRequest(id=1, name=张三)");
        assertEquals("{\"@type\":\"com.maxqiu.demo.request.UserRequest\",\"id\":1,\"name\":\"张三\"}", s);
    }

    @Test
    @Order(5)
    void multiParameter() {
        System.out.println(service.multiParameter(1, 2));
        String s = stringRedisTemplate.opsForValue().get("myCacheName::SimpleKey [1, 2]");
        assertEquals("3", s);
    }

    @Test
    @Order(6)
    void requestParameter2() {
        System.out.println(service.requestParameter2(new UserRequest(2, "李四"), "张"));
        String s = stringRedisTemplate.opsForValue().get("myCacheName::SimpleKey [UserRequest(id=2, name=李四), 张]");
        assertEquals("{\"@type\":\"com.maxqiu.demo.request.UserRequest\",\"id\":2,\"name\":\"李四\"}", s);
    }

    @Test
    @Order(7)
    void listParameter() {
        System.out.println(service.listParameter(Arrays.asList(1, 2, 3)));
        String s = stringRedisTemplate.opsForValue().get("myCacheName::1,2,3");
        assertEquals("[1,2,3]", s);
    }
}
