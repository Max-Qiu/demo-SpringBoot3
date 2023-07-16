package com.maxqiu.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import com.maxqiu.demo.properties.ElasticSearchProperties;

import jakarta.annotation.Resource;

/**
 * REST客户端是
 *
 * @author Max_Qiu
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Resource
    private ElasticSearchProperties properties;

    @Override
    public ClientConfiguration clientConfiguration() {
        // 使用构建器来提供集群地址
        return ClientConfiguration.builder()
            // 设置连接地址
            .connectedTo(properties.getHostAndPorts())
            // 启用ssl并配置CA指纹
            .usingSsl(properties.getCaFingerprint())
            // 设置用户名密码
            .withBasicAuth(properties.getUsername(), properties.getPassword())
            // 创建连接信息
            .build();
    }
}
