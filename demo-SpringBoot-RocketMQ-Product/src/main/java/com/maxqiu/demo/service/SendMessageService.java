package com.maxqiu.demo.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.maxqiu.demo.pojo.UserMessage;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Max_Qiu
 */
@Service
@Slf4j
public class SendMessageService {

    @Resource
    private RocketMQClientTemplate rocketMQClientTemplate;

    public void sendNormalMessage(String normalTopic) {
        SendReceipt sendReceipt = rocketMQClientTemplate.syncSendNormalMessage(normalTopic, new UserMessage(1, "name", (byte)3));
        System.out.printf("normalSend to topic %s sendReceipt=%s %n", normalTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendNormalMessage(normalTopic, "normal message");
        System.out.printf("normalSend to topic %s sendReceipt=%s %n", normalTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendNormalMessage(normalTopic, "byte message".getBytes(StandardCharsets.UTF_8));
        System.out.printf("normalSend to topic %s sendReceipt=%s %n", normalTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendNormalMessage(normalTopic, MessageBuilder.withPayload("test message".getBytes()).build());
        System.out.printf("normalSend to topic %s sendReceipt=%s %n", normalTopic, sendReceipt);
    }

    public void sendFIFOMessage(String fifoTopic, String messageGroup) {
        SendReceipt sendReceipt = rocketMQClientTemplate.syncSendFifoMessage(fifoTopic, new UserMessage(1, "name", (byte)3), messageGroup);
        System.out.printf("fifoSend to topic %s sendReceipt=%s %n", fifoTopic, sendReceipt);

        sendReceipt =
            rocketMQClientTemplate.syncSendFifoMessage(fifoTopic, MessageBuilder.withPayload("test message".getBytes()).build(), messageGroup);
        System.out.printf("fifoSend to topic %s sendReceipt=%s %n", fifoTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendFifoMessage(fifoTopic, "fifo message", messageGroup);
        System.out.printf("fifoSend to topic %s sendReceipt=%s %n", fifoTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendFifoMessage(fifoTopic, "byte message".getBytes(StandardCharsets.UTF_8), messageGroup);
        System.out.printf("fifoSend to topic %s sendReceipt=%s %n", fifoTopic, sendReceipt);
    }

    public void sendDelayMessage(String delayTopic) {
        SendReceipt sendReceipt =
            rocketMQClientTemplate.syncSendDelayMessage(delayTopic, new UserMessage(1, "name", (byte)3), Duration.ofSeconds(10));
        System.out.printf("delaySend to topic %s sendReceipt=%s %n", delayTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendDelayMessage(delayTopic, MessageBuilder.withPayload("test message".getBytes()).build(),
            Duration.ofSeconds(30));
        System.out.printf("delaySend to topic %s sendReceipt=%s %n", delayTopic, sendReceipt);

        sendReceipt = rocketMQClientTemplate.syncSendDelayMessage(delayTopic, "this is my message", Duration.ofSeconds(60));
        System.out.printf("delaySend to topic %s sendReceipt=%s %n", delayTopic, sendReceipt);

        sendReceipt =
            rocketMQClientTemplate.syncSendDelayMessage(delayTopic, "byte messages".getBytes(StandardCharsets.UTF_8), Duration.ofSeconds(90));
        System.out.printf("delaySend to topic %s sendReceipt=%s %n", delayTopic, sendReceipt);
    }

    public void aSycSendMessage(String normalTopic, String fifoTopic, String messageGroup, String delayTopic) {

        CompletableFuture<SendReceipt> future0 = new CompletableFuture<>();
        CompletableFuture<SendReceipt> future1 = new CompletableFuture<>();
        CompletableFuture<SendReceipt> future2 = new CompletableFuture<>();
        ExecutorService sendCallbackExecutor = Executors.newCachedThreadPool();

        future0.whenCompleteAsync((sendReceipt, throwable) -> {
            if (null != throwable) {
                log.error("Failed to send message", throwable);
                return;
            }
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        }, sendCallbackExecutor);

        future1.whenCompleteAsync((sendReceipt, throwable) -> {
            if (null != throwable) {
                log.error("Failed to send message", throwable);
                return;
            }
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        }, sendCallbackExecutor);

        future2.whenCompleteAsync((sendReceipt, throwable) -> {
            if (null != throwable) {
                log.error("Failed to send message", throwable);
                return;
            }
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        }, sendCallbackExecutor);

        CompletableFuture<SendReceipt> completableFuture0 =
            rocketMQClientTemplate.asyncSendNormalMessage(normalTopic, new UserMessage(1, "name", (byte)3), future0);
        System.out.printf("normalSend to topic %s sendReceipt=%s %n", normalTopic, completableFuture0);

        CompletableFuture<SendReceipt> completableFuture1 =
            rocketMQClientTemplate.asyncSendFifoMessage(fifoTopic, "fifo message", messageGroup, future1);
        System.out.printf("fifoSend to topic %s sendReceipt=%s %n", fifoTopic, completableFuture1);

        CompletableFuture<SendReceipt> completableFuture2 = rocketMQClientTemplate.asyncSendDelayMessage(delayTopic,
            "delay message".getBytes(StandardCharsets.UTF_8), Duration.ofSeconds(10), future2);
        System.out.printf("delaySend to topic %s sendReceipt=%s %n", delayTopic, completableFuture2);
    }

}
