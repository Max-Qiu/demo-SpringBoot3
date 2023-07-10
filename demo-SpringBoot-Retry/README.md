# 场景

当系统中调用一些第三方服务时（如使用 `http` 请求），如果第三方服务不是很稳定（比如网络波动），可以使用 `SpringBoot` 的自动重试功能

# 使用方法

## 依赖

在 `pom.xml` 中引入如下依赖

```xml
<!-- 引入重试 -->
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<!-- 额外添加aspectj -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```

必须要引入 `aspectj` ，否则会异常

## `Application` 启动类

在启动类中，需要添加 `@EnableRetry` 注解

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

// 启用重试
@EnableRetry
@SpringBootApplication
public class RetryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetryApplication.class, args);
    }
}

```

## `service` 服务类

主要使用以下三个注解，具体配置项见代码注解

- `@Retryable`：指定哪个方法执行重试
- `@Backoff`：延迟配置
- `@Recover`：最终回调处理

```java
import java.net.http.HttpConnectTimeoutException;
import java.time.LocalTime;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RetryService {

    /**
     * 需要执行重试的方法
     */
    @Retryable(
        // include：同value，当执行重试的异常类型（可以多个）
        // include = Exception.class,
        // exclude：要排除的异常类型（可以多个）
        // exclude = {},
        // 当 include 和 exclude 均为空时，所有异常均重试
        // maxAttempts：最大重试次数（包括第一次失败）
        maxAttempts = 5,
        // 重试配置
        backoff = @Backoff(
            // 延迟时间，单位：毫秒
            delay = 1000,
            // 最大延迟时间（默认值为0即不启用，若小于delay值则为3000），单位：毫秒
            maxDelay = 5000,
            // 相对上一次延迟时间的倍数（比如2：第一次1000毫秒，第二次2000毫秒，第三次4000毫秒...）
            multiplier = 2))
    public String retry(Integer code) throws Exception {
        log.info("方法被调用，时间：{}", LocalTime.now());
        // 这里可能会产生非检查异常
        int i = 2 / code;
        if (code == 1) {
            // 此处随意使用了一个检查异常
            throw new HttpConnectTimeoutException("抛出自定义异常信息！");
        }
        log.info("方法调用成功！，结果：{}", i);
        return "SUCCESS";
    }

    /**
     * 使用 @Recover 注解做最终失败处理（可以针对不同的异常定义多个最终失败处理）<br>
     * 第一个参数：需要处理的异常类型<br>
     * 后面的参数：（可选）与重试方法相同顺序和类型的参数<br>
     * 返回值类型：必须与重试方法的类型相同
     */
    @Recover
    public String recover(HttpConnectTimeoutException e, Integer code) {
        log.error("HttpConnectTimeoutException回调方法执行！");
        log.error("异常信息：{}", e.getMessage());
        log.error("参数code:{}", code);
        return "指定异常处理";
    }

    /**
     * 建议定义一个所有 Exception 异常处理，用于处理非检查异常
     */
    @Recover
    public String recover(Exception e) {
        log.error("Exception回调方法执行！");
        log.error("异常信息：{}", e.getMessage());
        return "所有异常处理";
    }
}
```

# 验证

编写测试类

```java
import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.maxqiu.demo.service.RetryService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
class RetryServiceTest {
    @Resource
    private RetryService retryService;

    @Test
    void retryTest() {
        try {
            log.info("调用方法，参数：{}", 0);
            log.info("返回结果：{}\n", retryService.retry(0));
            log.info("调用方法，参数：{}", 1);
            log.info("返回结果：{}\n", retryService.retry(1));
            log.info("调用方法，参数：{}", 2);
            log.info("返回结果：{}\n", retryService.retry(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

执行结果

```
2023-07-10T21:36:25.300+08:00  INFO 5660 --- [           main] com.maxqiu.demo.RetryServiceTest         : 调用方法，参数：0
2023-07-10T21:36:25.315+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:25.315383100
2023-07-10T21:36:26.322+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:26.322611600
2023-07-10T21:36:28.329+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:28.329357400
2023-07-10T21:36:32.337+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:32.337945800
2023-07-10T21:36:37.352+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:37.352717400
2023-07-10T21:36:37.353+08:00 ERROR 5660 --- [           main] com.maxqiu.demo.service.RetryService     : Exception回调方法执行！
2023-07-10T21:36:37.353+08:00 ERROR 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 异常信息：/ by zero
2023-07-10T21:36:37.353+08:00  INFO 5660 --- [           main] com.maxqiu.demo.RetryServiceTest         : 返回结果：所有异常处理

2023-07-10T21:36:37.353+08:00  INFO 5660 --- [           main] com.maxqiu.demo.RetryServiceTest         : 调用方法，参数：1
2023-07-10T21:36:37.353+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:37.353440100
2023-07-10T21:36:38.366+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:38.366952800
2023-07-10T21:36:40.371+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:40.371073100
2023-07-10T21:36:44.384+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:44.384432700
2023-07-10T21:36:49.399+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:49.399437400
2023-07-10T21:36:49.399+08:00 ERROR 5660 --- [           main] com.maxqiu.demo.service.RetryService     : HttpConnectTimeoutException回调方法执行！
2023-07-10T21:36:49.400+08:00 ERROR 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 异常信息：抛出自定义异常信息！
2023-07-10T21:36:49.400+08:00 ERROR 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 参数code:1
2023-07-10T21:36:49.400+08:00  INFO 5660 --- [           main] com.maxqiu.demo.RetryServiceTest         : 返回结果：指定异常处理

2023-07-10T21:36:49.400+08:00  INFO 5660 --- [           main] com.maxqiu.demo.RetryServiceTest         : 调用方法，参数：2
2023-07-10T21:36:49.400+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:36:49.400468300
2023-07-10T21:36:49.400+08:00  INFO 5660 --- [           main] com.maxqiu.demo.service.RetryService     : 方法调用成功！，结果：1
2023-07-10T21:36:49.400+08:00  INFO 5660 --- [           main] com.maxqiu.demo.RetryServiceTest         : 返回结果：SUCCESS
```

1. 在发生异常时，方法被调用了 5 次，说明重试生效了
2. 方法重试的时间间隔逐渐增大且不超过 5s ，说明延迟配置生效了
3. 指定的检查异常和非检查异常都可以捕获，说明回调生效了

# 注意事项

`@Retryable` 修饰的方法被同一个类的其他普通方法调用时不会生效，比如下文中调用 `callInSameClass` 会直接抛出异常：

```java
@Service
@Slf4j
public class RetryService {
    /**
     * 如果在同一个类中调用重试方法，重试注解不会生效
     */
    public String callInSameClass(int code) throws Exception {
        return retry(code);
    }

    /**
     * 需要执行重试的方法
     */
    @Retryable
    public String retry(Integer code, String name) throws Exception {
        ...
    }
}
```

```
2023-07-10T21:37:14.997+08:00  INFO 12964 --- [           main] com.maxqiu.demo.RetryServiceTest         : 调用方法，参数：0
2023-07-10T21:37:15.002+08:00  INFO 12964 --- [           main] com.maxqiu.demo.service.RetryService     : 方法被调用，时间：21:37:15.002027
java.lang.ArithmeticException: / by zero
......
```

正确的调用方式应该是在另一个类中调用重试方法

```java
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class CallService {
    @Resource
    private RetryService retryService;

    /**
     * 如果在另一个类中调用重试方法，可以生效
     */
    public String callInOtherClass(int code) throws Exception {
        return retryService.retry(code);
    }
}
```
