package com.maxqiu.demo.consumer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @author Max_Qiu
 */
@Service
@RocketMQMessageListener(endpoints = "${demo.rocketmq.endpoints}", accessKey = "${demo.rocketmq.access-key}",
    secretKey = "${demo.rocketmq.secret-key}", topic = "${demo.rocketmq.topic}", consumerGroup = "${demo.rocketmq.consumer-group}", tag = "*")
public class NormalConsumer implements RocketMQListener {

    @Override
    public ConsumeResult consume(MessageView messageView) {
        System.out.println("handle my fifo message:" + messageView);
        ByteBuffer byteBuffer = messageView.getBody();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        String s1 = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(s1);
        return ConsumeResult.SUCCESS;
    }

}
