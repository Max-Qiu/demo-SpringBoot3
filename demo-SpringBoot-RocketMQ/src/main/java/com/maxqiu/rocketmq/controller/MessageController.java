package com.maxqiu.rocketmq.controller;

import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maxqiu.rocketmq.producer.MessageProducer;

import jakarta.annotation.Resource;

/**
 * 测试消息发送的 REST 接口
 */
@RestController
@RequestMapping("/mq")
public class MessageController {

    @Resource
    private MessageProducer messageProducer;

    /**
     * 发送同步消息<br>
     * 访问示例：GET http://localhost:8080/mq/sync?msg=hello
     */
    @GetMapping("/sync")
    public String sendSync(@RequestParam(defaultValue = "Hello RocketMQ (Sync)") String msg) {
        SendResult result = messageProducer.sendSync(msg);
        return "同步消息发送成功！\nMsgId: " + result.getMsgId() + "\nSendStatus: " + result.getSendStatus();
    }

    /**
     * 发送异步消息<br>
     * 访问示例：GET http://localhost:8080/mq/async?msg=hello
     */
    @GetMapping("/async")
    public String sendAsync(@RequestParam(defaultValue = "Hello RocketMQ (Async)") String msg) {
        messageProducer.sendAsync(msg);
        return "异步消息已提交，请查看控制台日志确认发送结果。";
    }

    /**
     * 发送延迟消息<br>
     * 延迟级别：1=1s 2=5s 3=10s 4=30s 5=1m 6=2m 7=3m 8=4m 9=5m 10=6m 11=7m 12=8m 13=9m 14=10m 15=20m 16=30m 17=1h 18=2h<br>
     * 访问示例：GET http://localhost:8080/mq/delay?msg=hello&delayLevel=3
     */
    @GetMapping("/delay")
    public String sendDelay(@RequestParam(defaultValue = "Hello RocketMQ (Delay)") String msg, @RequestParam(defaultValue = "3") int delayLevel) {
        if (delayLevel < 1 || delayLevel > 18) {
            return "delayLevel 参数非法，有效范围为 1~18。";
        }
        SendResult result = messageProducer.sendDelay(msg, delayLevel);
        String[] levelDesc = {"", "1s", "5s", "10s", "30s", "1m", "2m", "3m", "4m", "5m", "6m", "7m", "8m", "9m", "10m", "20m", "30m", "1h", "2h"};
        return "延迟消息发送成功！\nMsgId: " + result.getMsgId() + "\nSendStatus: " + result.getSendStatus() + "\n预计延迟: " + levelDesc[delayLevel] + " 后被消费";
    }

    /**
     * 发送带 Tag 和 Keys 的消息（使用 Message<?> 封装）<br>
     * 访问示例：GET http://localhost:8080/mq/tag?msg=hello&tag=order&keys=ORDER-20240001
     */
    @GetMapping("/tag")
    public String sendWithTag(@RequestParam(defaultValue = "Hello RocketMQ (Tag)") String msg, @RequestParam(defaultValue = "order") String tag,
        @RequestParam(defaultValue = "ORDER-00001") String keys) {
        SendResult result = messageProducer.sendWithTagAndKeys(msg, tag, keys);
        return "带Tag消息发送成功！\nMsgId: " + result.getMsgId() + "\nSendStatus: " + result.getSendStatus() + "\nTag: " + tag + "\nKeys: " + keys;
    }

}
