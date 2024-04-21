package com.maxqiu.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Max_Qiu
 */
@RestController
@RequestMapping("")
public class IndexController {
    /**
     * 测试页
     */
    @GetMapping("")
    @ResponseBody
    public String hello() {
        return "这是首页！";
    }
}
