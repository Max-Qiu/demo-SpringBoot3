package com.maxqiu.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.maxqiu.demo.mapper")
@SpringBootApplication
public class SnowflakeIdApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnowflakeIdApplication.class, args);
    }

}
