package com.maxqiu.demo.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maxqiu.demo.entity.Demo;
import com.maxqiu.demo.mapper.DemoMapper;

/**
 * 示例 服务类
 *
 * @author Max_Qiu
 */
@Service
public class DemoService extends ServiceImpl<DemoMapper, Demo> {

}
