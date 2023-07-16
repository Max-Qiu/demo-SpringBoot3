package com.maxqiu.demo.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.maxqiu.demo.document.User;

/**
 * @author Max_Qiu
 */
public interface UserRepository extends ElasticsearchRepository<User, Integer> {
    List<User> findUsersByNameAndAddress(String name, String address);
}
