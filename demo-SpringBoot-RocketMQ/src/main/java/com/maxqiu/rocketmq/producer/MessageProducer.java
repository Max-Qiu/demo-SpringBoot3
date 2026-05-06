package com.maxqiu.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RocketMQ 消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    /**
     * Topic 名称（可根据实际情况修改）
     */
    public static final String TOPIC = "test-topic";

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     *
     * @param message
     *            消息内容
     * @return 发送结果
     */
    public SendResult sendSync(String message) {
        log.info("【生产者】发送同步消息，topic: {}，内容: {}", TOPIC, message);
        SendResult result = rocketMQTemplate.syncSend(TOPIC, message);
        log.info("【生产者】同步消息发送结果: {}", result);
        return result;
    }

    /**
     * 发送异步消息
     *
     * @param message
     *            消息内容
     */
    public void sendAsync(String message) {
        log.info("【生产者】发送异步消息，topic: {}，内容: {}", TOPIC, message);
        rocketMQTemplate.asyncSend(TOPIC, message, new org.apache.rocketmq.client.producer.SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("【生产者】异步消息发送成功: {}", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.error("【生产者】异步消息发送失败", e);
            }

        });
    }

    /**
     * 发送单向消息（不关心结果）
     *
     * @param message
     *            消息内容
     */
    public void sendOneWay(String message) {
        log.info("【生产者】发送单向消息，topic: {}，内容: {}", TOPIC, message);
        rocketMQTemplate.sendOneWay(TOPIC, message);
    }

    /**
     * 使用 Message<?> 发送消息（携带 Tag、Keys、自定义 Header）
     * <p>
     * 说明： - Tag：消息标签，消费者可按 Tag 过滤，仅消费感兴趣的消息 - Keys：业务唯一标识（如订单号），用于 RocketMQ 控制台查询消息轨迹 - 自定义 Header：可附带任意业务元数据，消费者端通过 MessageExt 读取
     * </p>
     *
     * @param payload
     *            消息体内容
     * @param tag
     *            消息标签（消费者可按 Tag 过滤）
     * @param keys
     *            业务唯一键（用于消息追踪，如订单号）
     * @return 发送结果
     */
    public SendResult sendWithTagAndKeys(String payload, String tag, String keys) {
        // destination 格式为 "topic:tag"，RocketMQ 会自动将 tag 设置到消息属性中
        String destination = TOPIC + ":" + tag;

        Message<String> message = MessageBuilder.withPayload(payload)
            // 设置 Keys（用于消息轨迹追踪，支持多个 key 用空格分隔）
            .setHeader(RocketMQHeaders.KEYS, keys)
            // 自定义业务 Header（消费者端可通过 MessageExt#getUserProperty 读取）
            .setHeader("source", "order-service").setHeader("version", "v1").build();

        log.info("【生产者】发送带Tag消息，destination: {}，keys: {}，内容: {}", destination, keys, payload);
        SendResult result = rocketMQTemplate.syncSend(destination, message);
        log.info("【生产者】带Tag消息发送结果: {}", result);
        return result;
    }

    /**
     * 发送延迟消息（基于延迟级别）
     * <p>
     * RocketMQ 延迟级别对应时间： 1=1s, 2=5s, 3=10s, 4=30s, 5=1m, 6=2m, 7=3m, 8=4m, 9=5m, 10=6m, 11=7m, 12=8m, 13=9m, 14=10m, 15=20m, 16=30m, 17=1h, 18=2h
     * </p>
     *
     * @param message
     *            消息内容
     * @param delayLevel
     *            延迟级别（1~18）
     * @return 发送结果
     */
    public SendResult sendDelay(String message, int delayLevel) {
        log.info("【生产者】发送延迟消息，topic: {}，delayLevel: {}，内容: {}", TOPIC, delayLevel, message);
        SendResult result = rocketMQTemplate.syncSend(TOPIC, MessageBuilder.withPayload(message).build(), 3000, delayLevel);
        log.info("【生产者】延迟消息发送结果: {}", result);
        return result;
    }

}
