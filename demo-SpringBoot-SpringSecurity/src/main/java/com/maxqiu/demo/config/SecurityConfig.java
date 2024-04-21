package com.maxqiu.demo.config;

import java.util.HashMap;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.alibaba.fastjson2.JSON;
import com.maxqiu.demo.entity.User;
import com.maxqiu.demo.service.UserService;

import jakarta.annotation.Resource;

/**
 * @author Max_Qiu
 */
@Configuration
public class SecurityConfig {
    @Resource
    private UserService userService;

    /**
     * 用户详细信息
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.getByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户名不存在！");
            }
            List<GrantedAuthority> auths = AuthorityUtils.createAuthorityList("admin", "user");
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), auths);
        };
    }

    /**
     * 指定密码的加密方式，这样在密码的前面就不需要添加{bcrypt}
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    /**
     * 自定义安全配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 登录
        http.formLogin(formLoginConfigurer -> {
            // 登录接口
            formLoginConfigurer.loginProcessingUrl("/auth/login");
            // 登录成功返回信息
            formLoginConfigurer.successHandler((request, response, authentication) -> {
                HashMap<String, Object> result = new HashMap<>();
                result.put("code", 0);// 成功
                result.put("message", "登录成功");
                result.put("data", authentication.getPrincipal());
                // 将结果对象转换成1s0n字符串
                String json = JSON.toJSONString(result);
                // 返回json数据到前端
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(json);
                response.flushBuffer();
            });
            // 登录失败返回信息
            formLoginConfigurer.failureHandler((request, response, exception) -> {
                HashMap<String, Object> result = new HashMap<>();
                result.put("code", -1);
                result.put("message", exception.getMessage());
                // 将结果对象转换成1s0n字符串
                String json = JSON.toJSONString(result);
                // 返回json数据到前端
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(json);
                response.flushBuffer();
            });
        });

        // 注销
        http.logout(logoutConfigurer -> {
            // 注销登录页面
            logoutConfigurer.logoutUrl("/auth/logout");
            // 注销登录处理器
            logoutConfigurer.logoutSuccessHandler((request, response, authentication) -> {
                HashMap<String, Object> result = new HashMap<>();
                result.put("code", 0);
                result.put("message", "注销成功");
                // 将结果对象转换成1s0n字符串
                String json = JSON.toJSONString(result);
                // 返回json数据到前端
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(json);
                response.flushBuffer();
            });
        });

        // 未登录返回信息
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
            .authenticationEntryPoint((request, response, authException) -> {
                HashMap<String, Object> result = new HashMap<>();
                result.put("code", -1);
                result.put("message", authException.getMessage());
                // 将结果对象转换成1s0n字符串
                String json = JSON.toJSONString(result);
                // 返回json数据到前端
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(json);
                response.flushBuffer();
            }));

        // 配置认证
        http.authorizeHttpRequests(urlRegistry -> {
            urlRegistry.requestMatchers("/auth/**").permitAll();
            urlRegistry.requestMatchers("/**").authenticated();
        });

        http.csrf(CsrfConfigurer::disable);
        return http.build();
    }
}
