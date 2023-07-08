package com.maxqiu.demo.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Max_Qiu
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NormalVO {
    /**
     * 不能为null
     */
    @NotNull
    private Integer id;

    /**
     * 不为null或者空
     */
    @NotEmpty
    private String notEmpty;

    /**
     * 大小
     */
    @Size(min = 6, max = 6)
    private String size;

    /**
     * 至少有一个非空白字符串
     */
    @NotBlank
    private String notBlank;

    /**
     * 判断标识符
     */
    @AssertTrue
    // @AssertFalse
    private Boolean flag;

    /**
     * 最大和最小值
     */
    @Max(100)
    @Min(10)
    private Integer number;

    /**
     * 最大和最小值（inclusive可设置是否包含边界值）
     */
    @DecimalMax(value = "100", inclusive = false)
    @DecimalMin(value = "10", inclusive = true)
    private Integer decimal;

    /**
     * 正数
     */
    @Positive
    private Integer positive;

    /**
     * 负数
     */
    @Negative
    private Integer negative;

    /**
     * 整数与小数的最大长度
     */
    @Digits(integer = 5, fraction = 3)
    private BigDecimal digits;

    /**
     * 将来的时间
     */
    @Future
    // 使用指定格式接收数据
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime future;

    /**
     * 过去的时间
     */
    @Past
    // 使用指定格式接收数据
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime past;

    /**
     * 正则
     *
     * 例：只能是数字和字母
     */
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String pattern;

    /**
     * 邮箱
     */
    @Email
    private String email;

    /**
     * 是一个URL连接
     */
    @URL
    private String url;
}
