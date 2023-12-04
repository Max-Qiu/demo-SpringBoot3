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
        // 注：实体设置注解后，启动时会自动创建索引，所以会显示存在
        boolean exists = template.indexOps(User.class).exists();
        System.out.println("索引是否存在：" + exists);
    }

    @Test
    @Order(2)
    void delete() {
        boolean delete = template.indexOps(User.class).delete();
        System.out.println("索引是否删除：" + delete);
    }

    @Test
    @Order(3)
    void create() {
        // 注：因为上一个测试方法删除了索引，所以这里可以手动创建索引，一般情况下不会用到
        boolean flag = template.indexOps(User.class).create();
        System.out.println("索引创建结果：" + flag);
    }

    @Test
    @Order(4)
    void mapping() {
        boolean mapping = template.indexOps(User.class).putMapping();
        System.out.println("映射修改结果：" + mapping);
    }
}
