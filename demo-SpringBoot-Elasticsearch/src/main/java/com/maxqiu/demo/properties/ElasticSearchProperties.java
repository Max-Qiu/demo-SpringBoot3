package com.maxqiu.demo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 邮箱配置
 *
 * @author Max_Qiu
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ElasticSearchProperties {
    /**
     * 是否启用
     */
    private String[] hostAndPorts;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * CA证书指纹
     */
    private String caFingerprint;
}
