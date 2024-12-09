package com.maxqiu.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.maxqiu.demo.entity.Demo;

/**
 * @author Max_Qiu
 */
@SpringBootTest
class DemoServiceTest {

    @Test
    void insert() {
        Demo demo = new Demo();
        demo.insert();
        System.out.println(demo.getId());
    }

}
