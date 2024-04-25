package com.maxqiu.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maxqiu.demo.entity.User;
import com.maxqiu.demo.service.UserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Max_Qiu
 */
@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController {
    @Resource
    private UserService userService;

    @PostMapping("register")
    public String register(@RequestParam String username, @RequestParam String password) {
        System.out.println("进入了注册方法");
        User byUsername = userService.getByUsername(username);
        if (byUsername != null) {
            return "用户名已存在";
        } else {
            userService.create(username, password);
        }
        return "这是 POST 注册方法";
    }
}
