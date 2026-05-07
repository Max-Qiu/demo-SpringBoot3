package com.maxqiu.rocketmq.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者三：只消费 Tag = "order" 的消息
 * <p>
 * 通过 tag 属性实现 Tag 过滤： - 单个Tag：tag = "order" - 多个Tag：tag = "order || payment || refund" - 全部Tag：tag = "*"
 * </p>
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "test-normal-topic", consumerGroup = "rmq-consumer-group-order", tag = "order" // 只订阅 Tag = "order" 的消息
)
public class OrderMessageConsumer implements RocketMQListener {

    @Override
    public ConsumeResult consume(MessageView msg) {
        String body = StandardCharsets.UTF_8.decode(msg.getBody()).toString();
        log.info("【消费者-普通消息-仅ORDER标签】收到订单消息: MsgId={}, Keys={}, Body={}", msg.getMessageId(), String.join(",", msg.getKeys()), body);
        // 只有 Tag="order" 的消息才会到达这里
        return ConsumeResult.SUCCESS;
    }

}
