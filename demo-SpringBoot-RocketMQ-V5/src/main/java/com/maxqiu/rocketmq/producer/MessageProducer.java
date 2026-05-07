package com.maxqiu.rocketmq.producer;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.apache.rocketmq.client.support.RocketMQHeaders;
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

    private final RocketMQClientTemplate rocketMQClientTemplate;

    /**
     * 发送同步消息
     *
     * @param message
     *            消息内容
     * @return SendReceipt 发送回执（包含 MessageId）
     */
    public SendReceipt sendSync(String message) {
        log.info("【生产者】发送同步消息，topic: {}，内容: {}", "test-normal-topic", message);
        SendReceipt receipt = rocketMQClientTemplate.syncSendNormalMessage("test-normal-topic", message);
        log.info("【生产者】同步消息发送成功: MsgId={}", receipt.getMessageId());
        return receipt;
    }

    /**
     * 发送异步消息
     *
     * @param message
     *            消息内容
     */
    public void sendAsync(String message) {
        log.info("【生产者】发送异步消息，topic: {}，内容: {}", "test-normal-topic", message);
        CompletableFuture<SendReceipt> future = rocketMQClientTemplate.asyncSendNormalMessage("test-normal-topic", message, null);
        future.whenComplete((receipt, e) -> {
            if (e != null) {
                log.error("【生产者】异步消息发送失败", e);
            } else {
                log.info("【生产者】异步消息发送成功: MsgId={}", receipt.getMessageId());
            }
        });
    }

    /**
     * 使用 Message<?> 发送消息（携带 Tag、Keys、自定义 Header）
     * <p>
     * - Tag：消息标签，消费者可按 Tag 过滤，destination 格式为 "topic:tag" - Keys：业务唯一标识，通过 RocketMQHeaders.KEYS 设置 - 自定义属性：消费者通过 MessageView#getProperties() 读取
     * </p>
     *
     * @param payload
     *            消息体内容
     * @param tag
     *            消息标签（消费者可按 Tag 过滤）
     * @param keys
     *            业务唯一键（用于消息追踪，如订单号）
     * @return SendReceipt 发送回执
     */
    public SendReceipt sendWithTagAndKeys(String payload, String tag, String keys) {
        // destination 格式为 "topic:tag"，框架会自动将 tag 写入消息属性
        String destination = "test-normal-topic" + ":" + tag;

        Message<String> message = MessageBuilder.withPayload(payload)
            // 设置 Keys（用于消息追踪）
            .setHeader(RocketMQHeaders.KEYS, keys)
            // 自定义属性（消费者端通过 MessageView#getProperties 读取）
            .setHeader("source", "order-service").setHeader("version", "v1").build();

        log.info("【生产者】发送带Tag消息，destination: {}，keys: {}，内容: {}", destination, keys, payload);
        SendReceipt receipt = rocketMQClientTemplate.syncSendNormalMessage(destination, message);
        log.info("【生产者】带Tag消息发送成功: MsgId={}", receipt.getMessageId());
        return receipt;
    }

    /**
     * 发送延迟消息
     *
     * @param message
     *            消息内容
     * @param delaySeconds
     *            延迟秒数（大于 0）
     * @return SendReceipt 发送回执
     */
    public SendReceipt sendDelay(String message, long delaySeconds) {
        log.info("【生产者】发送延迟消息，topic: {}，delaySeconds: {}，内容: {}", "test-delay-topic", delaySeconds, message);
        SendReceipt receipt = rocketMQClientTemplate.syncSendDelayMessage("test-delay-topic", message, Duration.ofSeconds(delaySeconds));
        log.info("【生产者】延迟消息发送成功: MsgId={}", receipt.getMessageId());
        return receipt;
    }

}
