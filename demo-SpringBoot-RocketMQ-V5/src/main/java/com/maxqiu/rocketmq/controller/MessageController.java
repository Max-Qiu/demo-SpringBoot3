package com.maxqiu.rocketmq.controller;

import org.apache.rocketmq.client.apis.producer.SendReceipt;
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
        SendReceipt receipt = messageProducer.sendSync(msg);
        return "同步消息发送成功！\nMsgId: " + receipt.getMessageId();
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
     * 发送带 Tag 和 Keys 的消息<br>
     * 访问示例：GET http://localhost:8080/mq/tag?msg=hello&tag=order&keys=ORDER-20240001
     */
    @GetMapping("/tag")
    public String sendWithTag(@RequestParam(defaultValue = "Hello RocketMQ (Tag)") String msg, @RequestParam(defaultValue = "order") String tag,
        @RequestParam(defaultValue = "ORDER-00001") String keys) {
        SendReceipt receipt = messageProducer.sendWithTagAndKeys(msg, tag, keys);
        return "带Tag消息发送成功！\nMsgId: " + receipt.getMessageId() + "\nTag: " + tag + "\nKeys: " + keys;
    }

    /**
     * 发送延迟消息<br>
     * 访问示例：GET http://localhost:8080/mq/delay?msg=hello&delaySeconds=10
     */
    @GetMapping("/delay")
    public String sendDelay(@RequestParam(defaultValue = "Hello RocketMQ (Delay)") String msg, @RequestParam(defaultValue = "10") long delaySeconds) {
        if (delaySeconds < 1) {
            return "delaySeconds 参数非法，需大于 0。";
        }
        SendReceipt receipt = messageProducer.sendDelay(msg, delaySeconds);
        return "延迟消息发送成功！\nMsgId: " + receipt.getMessageId() + "\n预计延迟: " + delaySeconds + " 秒后被消费";
    }

}
