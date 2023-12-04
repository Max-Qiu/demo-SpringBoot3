> 官方文档：[https://spring.io/projects/spring-session](https://spring.io/projects/spring-session)

# 简介

`Spring Session` 提供了一个 API 和实现来管理用户的会话信息，同时也使支持集群会话变得简单，而无需绑定到应用程序容器特定的解决方案。

`Spring Session` 集成了如下环境

- `HttpSession`：常用的 SpringMVC 模式，例如 Tomcat。
- `WebSocket`：WebSocket
- `WebSession` ：Spring WebFlux 模式

存储 `Session` 的数据源有如下方案

- `Reids`
- `JDBC`
- `MongoDB`
- `geode`

本文以 `HttpSession + Reids` 为例

# 版本说明

针对不同的 `SpringBoot` 主版本， `Spring Session` 的版本也不同，不过配置方式基本差不多，也可以跨版本会话共享

| SpringBoot | Spring Session | JDK  |
| ---------- | -------------- | ---- |
| 2.7.x      | 2.7.x          | 1.8  |
| 3.1.x      | 3.1.x          | 17   |

# Maven 依赖

```xml
<!-- Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- SpringSession Redis 数据源 -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!-- fastjson2 -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.42</version>
</dependency>
<!-- fastjson序列化扩展（SpringBoot3.x） -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2-extension-spring6</artifactId>
    <version>2.0.42</version>
</dependency>
<!-- fastjson序列化扩展（SpringBoot2.x） -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2-extension-spring5</artifactId>
    <version>2.0.42</version>
</dependency>
```

# 配置

## Spring Session 配置

1. 针对 `Spring Session` 有两种配置方式，可以使用 `yml` 文件，也可以使用 `@EnableRedisHttpSession` 注解，但同时只有一种方式生效，注解方式的优先级高于 `yml` 方式
2. 不论使用什么方式配置 `Spring Session` ， `Redis` 是一定要配置的，参考 [SpringBoot 整合 Redis](https://maxqiu.com/article/detail/102)
3. 主要配置 `Session` 的超时时间以及 `key` 的命名空间，其他配置如会话刷新模式、会话保存模式保持默认即可

### yml 方式

```yml
spring:
  session:
    timeout: 1800 # 会话超时时间，单位：秒（默认 1800 秒）
    redis:
      namespace: demo::session # 用于存储会话的键的命名空间（默认 spring::session）
```

### 注解方式

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

// 启用Redis存储HTTP的Session
@EnableRedisHttpSession(
    // 会话超时时间，单位：秒（默认 1800 秒）
    redisNamespace = "demo::session",
    // 用于存储会话的键的命名空间（默认 spring::session）
    maxInactiveIntervalInSeconds = 3600)
@SpringBootApplication
public class SpringSessionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSessionApplication.class, args);
    }
}

```

## 其他配置

### Redis 序列化设置

注意：该序列化仅针对 `Session` 的序列化，与 `RedisTemplate` 的设置不通用

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;

/**
 * Session 序列化设置
 */
@Configuration
public class SessionConfig {
    /**
     * 使用 fastjson 序列号
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }
}
```

### 浏览器 Cookie 设置

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Cookie配置
 */
@Configuration
public class CookieConfig {
    /**
     * 设置Cookie序列化的配置
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        // Session 的 key ，默认：SESSION
        serializer.setCookieName("SESSION");
        // Session 的 value 是否进行 Base64 编码，推荐关闭，方便浏览器内查看到 value 值
        serializer.setUseBase64Encoding(false);
        // Session 的路径
        serializer.setCookiePath("/");
        // Session 的可读域名（默认当前域名）若需要多域名共享 Cookie ，则需要设置为主域名
        // serializer.setDomainName("demo.com");
        return serializer;
    }
}
```

推荐阅读：

- [cookie设置域名问题，cookie跨域](https://blog.csdn.net/czhphp/article/details/65628977)
- [java之Cookie详解](https://www.cnblogs.com/z941030/p/4742188.html)

# 案例

## Session读写示例

```
/**
 * Session读写
 *
 * @author Max_Qiu
 */
@RestController
public class IndexController {
    @Value("${server.port}")
    private String port;

    @RequestMapping("get")
    public String get(HttpSession session) {
        String name = (String)session.getAttribute("name");
        return "port:" + port + "\tname:" + (name != null ? name : "null");
    }

    @RequestMapping("set")
    public String set(HttpSession session) {
        session.setAttribute("name", "max");
        return "ok";
    }
}
```

## 多服务示例（集群和微服务）

1. 启动服务，再使用 `--server.port=8081` 参数启动第二个服务
2. 本地 `Nginx` 配置如下，并启动 `Nginx` 服务
```
    upstream demo{
        server 127.0.0.1:8080;
        server 127.0.0.1:8081;
    }

    server {
        listen       80;
        location / {
            proxy_pass http://demo/;
            add_header Strict-Transport-Security "max-age=31536000";
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header Host $http_host;
            proxy_set_header HTTP_X_FORWARDED_FOR $remote_addr;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_redirect default;
        }
    }
```
3. 访问 `http://127.0.0.1/set` 写 `Session`
4. 访问 `http://127.0.0.1/get` 读 `Session` ，多次访问可以看到不同服务均可以读取 `Session` 中的值
