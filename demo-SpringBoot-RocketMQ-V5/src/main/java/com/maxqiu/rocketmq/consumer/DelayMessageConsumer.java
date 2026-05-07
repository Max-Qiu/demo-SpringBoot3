package com.maxqiu.rocketmq.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者二：订阅延时消息（不过滤 Tag）
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "test-delay-topic", consumerGroup = "rmq-consumer-group-all", tag = "*" // "*" 表示接收所有 Tag
)
public class DelayMessageConsumer implements RocketMQListener {

    @Override
    public ConsumeResult consume(MessageView msg) {
        String body = StandardCharsets.UTF_8.decode(msg.getBody()).toString();
        log.info("""
            【消费者-延迟消息】收到消息:
              MsgId  = {}
              Tag    = {}
              Keys   = {}
              source = {}
              version= {}
              Body   = {}""", msg.getMessageId(), msg.getTag().orElse(""), String.join(",", msg.getKeys()), msg.getProperties().get("source"),
            msg.getProperties().get("version"), body);
        // 返回 SUCCESS 表示消费成功，FAILURE 表示消费失败（消息将被重投）
        return ConsumeResult.SUCCESS;
    }

}
