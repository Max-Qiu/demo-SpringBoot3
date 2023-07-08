package com.maxqiu.demo.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户
 *
 * @author Max_Qiu
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserVO {
    @NotNull
    private Integer id;
    @NotBlank
    private String name;
    @Valid
    @NotNull
    private AddressVO address;
}
