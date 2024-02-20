> 官方教程：[Spring Data Elasticsearch - Reference Documentation](https://docs.spring.io/spring-data/elasticsearch/docs/5.1.6/reference/html/)

PS：本文只是一篇极其简单的连接配置整合教程，不涉及搜索示例，建议认真阅读官方文档

# 版本对应关系

官方文档：[Versions](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html)

| Spring Data Release Train | Spring Data Elasticsearch | Elasticsearch | Spring Framework | Spring Boot |
| :-----------------------: | :-----------------------: | :-----------: | :--------------: | :---------: |
|     2023.1 (Vaughan)      |           5.2.x           |    8.11.4     |      6.1.x       |    3.2.x    |
|     2023.0 (Ullmann)      |           5.1.x           |     8.7.1     |      6.0.x       |    3.1.x    |
|      2022.0 (Turing)      |           5.0.x           |     8.5.3     |      6.0.x       |    3.0.x    |
|       2021.2 (Raj)        |           4.4.x           |    7.17.3     |      5.3.x       |    2.7.x    |
|        2021.1 (Q)         |           4.3.x           |    7.15.2     |      5.3.x       |    2.6.x    |
|      2021.0 (Pascal)      |           4.2.x           |    7.12.0     |      5.3.x       |    2.5.x    |
|      2020.0 (Ockham)      |           4.1.x           |     7.9.3     |      5.3.2       |    2.4.x    |
|          Neumann          |           4.0.x           |     7.6.2     |      5.2.12      |    2.3.x    |
|           Moore           |           3.2.x           |    6.8.12     |      5.2.12      |    2.2.x    |
|         Lovelace          |           3.1.x           |     6.2.2     |      5.1.19      |    2.1.x    |
|            Kay            |           3.0.x           |     5.5.0     |      5.0.13      |    2.0.x    |
|          Ingalls          |           2.1.x           |     2.4.0     |      4.3.25      |    1.5.x    |

本文以 `Spring Boot 3.2.x` 为例

# pom.xml

```xml
<!--核心依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
<!-- 自定义配置文件 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

# 连接信息配置

由于 `ES 8.x` 开始，服务安装后默认使用 `HTTPS` 协议访问，所以要自定义配置文件配置CA指纹信息

## 获取 CA 指纹

`Elasticearch` 安装完成后，在安装目录下的 `config/certs` 目录下，有个 `http_ca.crt` 根证书文件，使用以下命令可以获取指纹信息：

```bash
$ openssl x509 -in http_ca.crt -sha256 -fingerprint | grep SHA256 | sed 's/://g'
SHA256 Fingerprint=8ADC8194ADFBC5AA5006B787988F207857CB7B935EE9B5C707C6BAB84759A99C
```

注：

- `Linux` 系统自带 `openssl` 指令，可以直接执行
- `Windows` 系统需要借助 `git` 安装完成后附带的 `Git Bash` 工具执行以上指令
- `macOS` 系统需要把 `SHA256` 换成小写 `sha256`
- `Docker` 环境先进入容器再执行，证书文件在 `/usr/share/elasticsearch/config/certs/` 目录下

## Properties

自定一个配置文件，方便在 `yml` 配置客户端连接信息

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 客户端连接信息配置
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ElasticSearchProperties {
    /**
     * 主机地址
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

```

## application.yml

将连接信息放在 `yml` 文件中，方便根据运行环境切换

```yml
elasticsearch:
  hostAndPorts: # elasticsearch 连接地址，可填写多个
    - 127.0.0.1:9200
  username: elastic
  password: password
  ca-fingerprint: 8ADC8194ADFBC5AA5006B787988F207857CB7B935EE9B5C707C6BAB84759A99C
```

## Configuration

自定义一个 `Bean` 对象，配置连接。更多连接信息可以跟进 `org.springframework.data.elasticsearch.client.ClientConfigurationBuilder` 中可配置的属性进行扩展

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import com.maxqiu.demo.properties.ElasticSearchProperties;

import jakarta.annotation.Resource;

/**
 * Elasticsearch客户端
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
```

# 增删改查

用的不多，不是很熟悉，不写了。。。。。。