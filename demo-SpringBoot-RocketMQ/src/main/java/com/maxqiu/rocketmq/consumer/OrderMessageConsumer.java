package com.maxqiu.rocketmq.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者二：只消费 Tag = "order" 的消息
 * <p>
 * 通过 selectorExpression 实现 Tag 过滤： - 单个Tag：selectorExpression = "order" - 多个Tag：selectorExpression = "order || payment || refund"
 * </p>
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "test-topic", consumerGroup = "rmq-consumer-group-order", selectorExpression = "order" // 只订阅 Tag = "order"
                                                                                                                        // 的消息
)
public class OrderMessageConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt msg) {
        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        log.info("【消费者-ORDER】收到订单消息: MsgId={}, Keys={}, Body={}", msg.getMsgId(), msg.getKeys(), body);
        // 只有 Tag="order" 的消息才会到达这里
    }

}
