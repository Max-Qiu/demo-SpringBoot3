package com.maxqiu.demo.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.maxqiu.demo.document.User;

/**
 * 基于Repository的增删改查
 *
 * @author Max_Qiu
 */
@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    @Order(1)
    void save() {
        User user = new User(1, "张三", 18, "上海市闵行区", LocalDateTime.now());
        User save = repository.save(user);
        System.out.println(save);
    }

    @Test
    @Order(2)
    void findById() {
        Optional<User> optionalUser = repository.findById(1);
        optionalUser.ifPresent(System.out::println);
    }

    @Test
    @Order(3)
    void existsById() {
        boolean b = repository.existsById(1);
        System.out.println(b);
    }

    @Test
    @Order(4)
    void deleteById() {
        repository.deleteById(1);
    }

    @Test
    @Order(5)
    void saveAll() {
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
        Iterable<User> users = repository.saveAll(userList);
        users.forEach(System.out::println);
    }

    @Test
    @Order(6)
    void count() {
        long count = repository.count();
        System.out.println(count);
    }

    @Test
    @Order(7)
    void findAll() {
        Iterable<User> all = repository.findAll();
        all.forEach(System.out::println);
    }

    @Test
    @Order(8)
    void page() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<User> page = repository.findAll(pageRequest);
        for (User user : page) {
            System.out.println(user);
        }
    }

    @Test
    @Order(9)
    void sort() {
        Iterable<User> users = repository.findAll(Sort.by(Sort.Order.desc("age")));
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    @Order(10)
    void findAllById() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Iterable<User> users = repository.findAllById(list);
        users.forEach(System.out::println);
    }

    @Test
    @Order(11)
    void deleteByUser() {
        User user = new User();
        user.setId(1);
        repository.delete(user);
    }

    @Test
    @Order(12)
    void deleteByIds() {
        List<User> list = new ArrayList<>();
        list.add(new User(2));
        list.add(new User(3));
        repository.deleteAll(list);
    }

    @Test
    @Order(13)
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    @Order(14)
    void test() {
        List<User> userList = repository.findUsersByNameAndAddress("张三", "上海");
        for (User user : userList) {
            System.out.println(user);
        }
    }
}
