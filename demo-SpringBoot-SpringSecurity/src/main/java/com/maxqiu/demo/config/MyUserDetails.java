package com.maxqiu.demo.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.maxqiu.demo.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 自定义的用户信息
 *
 * @author Max_Qiu
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MyUserDetails implements UserDetails {
    private Long id;

    private String username;

    private String password;

    private List<GrantedAuthority> auths;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public MyUserDetails(User entity, List<GrantedAuthority> auths) {
        this.setId(entity.getId());
        this.setUsername(entity.getUsername());
        this.setPassword(entity.getPassword());
        this.setAuths(auths);
    }
}
