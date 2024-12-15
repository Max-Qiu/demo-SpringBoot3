> 参考文档

- （错误复盘）[MybatisPlus雪花算法生成器Id重复生成](https://www.tpfuture.top/views/issue/MybatisPlusSequenceCollision.html)
- （解决方案）[mybatis-plus id在高并发下出现重复](https://www.cnblogs.com/zmj-pr/p/16856864.html)
- （解决方案）[基于Redis解决集群环境下雪花算法机器号的分配问题](https://cloud.tencent.com/developer/article/1861288)

话不多说，直接上代码

# 代码

## POM

```xml
<!-- Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- MySQL驱动 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
<!-- MybatisPlus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.9</version>
</dependency>
<!-- Redisson -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.39.0</version>
</dependency>
```

## 自定义ID生成器

```java
import java.util.concurrent.TimeUnit;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Sequence;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Number nextId(Object entity) {
        return getSequence().nextId();
    }

    private volatile Sequence sequence;

    private Long id;

    public Sequence getSequence() {
        if (sequence == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (sequence == null) {
                    RLock lock = redissonClient.getLock("snowflake:lock");
                    try {
                        if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                            RAtomicLong atomicLong = redissonClient.getAtomicLong("snowflake:id");
                            // 最终获取的序号ID
                            long id;
                            // 累计获取的次数
                            int i = 0;
                            do {
                                i++;
                                // 如果累计获取超过1024次，则说明没有可以使用的ID了，抛出异常
                                if (i > 1024) {
                                    log.error("获取不到可以使用的雪花ID序号");
                                    throw new RuntimeException("获取不到可以使用的雪花ID序号");
                                }
                                // 获取ID
                                id = atomicLong.incrementAndGet();
                                if (id >= 1024) {
                                    id = 0;
                                    atomicLong.set(0);
                                }
                                // 检查是否已被占用
                                String watchDogKey = "snowflake:id:" + id;
                                if (stringRedisTemplate.hasKey(watchDogKey)) {
                                    log.warn("当前雪花ID序号：{}已被使用", id);
                                } else {
                                    // 未使用，设置占用标记
                                    stringRedisTemplate.opsForValue().set(watchDogKey, "", 65, TimeUnit.SECONDS);
                                    this.id = id;
                                    break;
                                }
                            } while (true);
                            long workerId = id / 32;
                            long datacenterId = id % 32;
                            log.info("当前雪花ID序号为：{},对应的workerId为：{},对应的datacenterId为：{}", id, workerId, datacenterId);
                            sequence = new Sequence(workerId, datacenterId);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                            lock.unlock();
                            log.debug("lock released");
                        }
                    }
                }
            }
        }
        return sequence;
    }

    @Scheduled(fixedDelay = 20000)
    public void scheduledTask() {
        if (id == null) {
            log.debug("雪花ID序号未初始化，无需执行");
            return;
        }
        // 更新占用
        stringRedisTemplate.opsForValue().set("snowflake:id:" + id, "", 65, TimeUnit.SECONDS);
    }

}
```

# 测试

## 实体

```java
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 示例
 *
 * @author Max_Qiu
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("demo")
public class Demo extends Model<Demo> {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
```

## 测试类

```
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.maxqiu.demo.entity.Demo;

/**
 * @author Max_Qiu
 */
@SpringBootTest
class DemoServiceTest {

    @Test
    void insert() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                Demo demo = new Demo();
                demo.insert();
                System.out.println(demo.getId());
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

}
```

## 输出

```
2024-12-15T18:58:00.882+08:00  INFO 15392 --- [pool-2-thread-3] c.m.demo.config.SnowflakeIdGenerator     : 当前雪花ID序号为：26,对应的workerId为：0,对应的datacenterId为：26
2024-12-15T18:58:00.902+08:00  INFO 15392 --- [pool-2-thread-3] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2024-12-15T18:58:01.074+08:00  INFO 15392 --- [pool-2-thread-3] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@9706daa
2024-12-15T18:58:01.076+08:00  INFO 15392 --- [pool-2-thread-3] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
1868249143658676227
1868249143658676228
1868249143658676226
```

