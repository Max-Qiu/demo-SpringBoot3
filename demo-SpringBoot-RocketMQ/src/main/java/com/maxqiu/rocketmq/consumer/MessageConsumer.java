package com.maxqiu.rocketmq.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者一：订阅所有消息（不过滤 Tag）
 * <p>
 * 接收类型使用 MessageExt，可获取完整的消息元数据： - getMsgId() 消息唯一ID - getTags() 消息 Tag - getKeys() 消息 Keys（业务唯一键） - getUserProperty() 自定义 Header（生产者通过
 * MessageBuilder.setHeader 设置的属性） - getBody() 消息体字节数组
 * </p>
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "test-topic", consumerGroup = "rmq-consumer-group-all"
// selectorType 默认为 TAG，selectorExpression 默认为 "*" 表示接收所有 Tag
)
public class MessageConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt msg) {
        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        log.info(
            "【消费者-ALL】收到消息:" + "\n  MsgId  = {}" + "\n  Tag    = {}" + "\n  Keys   = {}" + "\n  source = {}" + "\n  version= {}" + "\n  Body   = {}",
            msg.getMsgId(), msg.getTags(), msg.getKeys(), msg.getUserProperty("source"), // 读取自定义 Header
            msg.getUserProperty("version"), // 读取自定义 Header
            body);
    }

}
