package com.maxqiu.demo.service;

import com.maxqiu.demo.properties.TestProperties;

import jakarta.annotation.Resource;

/**
 * 服务类
 *
 * @author Max_Qiu
 */
public class TestService {
    @Resource
    private TestProperties testProperties;

    public void test() {
        System.out.println("地址：" + testProperties.getAddress() + "\t秘钥：" + testProperties.getKey());
    }
}
