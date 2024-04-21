package com.maxqiu.demo.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxqiu.demo.entity.User;
import com.maxqiu.demo.mapper.UserMapper;

import jakarta.annotation.Resource;

/**
 * 用户 服务类
 *
 * @author Max_Qiu
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 根据用户名查找用户
     */
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    /**
     * 创建用户
     */
    public void create(String username, String password) {
        User entity = new User();
        entity.setUsername(username);
        entity.setPassword(bCryptPasswordEncoder.encode(password));
        save(entity);
    }
}
