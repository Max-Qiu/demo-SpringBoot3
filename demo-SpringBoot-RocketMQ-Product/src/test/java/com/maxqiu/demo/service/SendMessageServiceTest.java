package com.maxqiu.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

/**
 * @author Max_Qiu
 */
@SpringBootTest
class SendMessageServiceTest {

    @Resource
    private SendMessageService sendMessageService;

    @Test
    void sendNormalMessage() {
        sendMessageService.sendNormalMessage("normalTopic");
    }

    @Test
    void sendFIFOMessage() {
        sendMessageService.sendFIFOMessage("fifoTopic", "fifoGroup");
    }

    @Test
    void sendDelayMessage() {
        sendMessageService.sendDelayMessage("delayTopic");
    }

    @Test
    void aSycSendMessage() {
        sendMessageService.aSycSendMessage("normalTopic", "fifoTopic", "messageGroup", "delayTopic");
    }

}
