package com.maxqiu.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import com.maxqiu.demo.document.User;

/**
 * 文档增删改查
 *
 * @author Max_Qiu
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentApiTest {
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    @Order(1)
    void saveOne() {
        User user = new User(1, "张三", 18, "上海市闵行区", LocalDateTime.now());
        template.save(user);
    }

    @Test
    @Order(2)
    void getById() {
        User user = template.get("1", User.class);
        System.out.println(user);
    }

    @Test
    @Order(3)
    void deleteById() {
        String delete = template.delete("1", User.class);
        System.out.println(delete);
    }

    @Test
    @Order(4)
    void saveList() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "Max", 16, "上海市闵行区", LocalDateTime.now()));
        userList.add(new User(2, "Vicky", 17, "上海市长宁区", LocalDateTime.now()));
        userList.add(new User(3, "张三", 18, "上海张江", LocalDateTime.now()));
        userList.add(new User(4, "李四", 19, "江苏省盐城市", LocalDateTime.now()));
        userList.add(new User(5, "王五", 20, "江苏省苏州斯", LocalDateTime.now()));
        userList.add(new User(6, "赵六", 21, "江苏省南京市", LocalDateTime.now()));
        userList.add(new User(7, "孙七", 22, "北京市朝阳区", LocalDateTime.now()));
        userList.add(new User(8, "周八", 23, "山东济南", LocalDateTime.now()));
        userList.add(new User(9, "吴九", 24, "河南郑州", LocalDateTime.now()));
        userList.add(new User(10, "郑十", 25, "四川重庆", LocalDateTime.now()));
        Iterable<User> users = template.save(userList);
        for (User user : users) {
            System.out.println(user);
        }
    }
}
