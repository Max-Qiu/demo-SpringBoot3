package com.maxqiu.demo.config;

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
