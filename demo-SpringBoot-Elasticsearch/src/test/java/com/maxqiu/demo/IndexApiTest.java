package com.maxqiu.demo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import com.maxqiu.demo.document.User;

/**
 * 索引增删改查
 *
 * @author Max_Qiu
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IndexApiTest {
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    @Order(1)
    void exists() {
        boolean exists = template.indexOps(User.class).exists();
        System.out.println("索引是否存在：" + exists);
    }

    @Test
    @Order(1)
    void delete() {
        boolean delete = template.indexOps(User.class).delete();
        System.out.println("索引是否删除：" + delete);
    }

    @Test
    @Order(1)
    void create() {
        boolean flag = template.indexOps(User.class).create();
        System.out.println("索引创建结果：" + flag);
    }

    @Test
    @Order(1)
    void mapping() {
        boolean mapping = template.indexOps(User.class).putMapping();
        System.out.println("映射修改结果：" + mapping);
    }
}
