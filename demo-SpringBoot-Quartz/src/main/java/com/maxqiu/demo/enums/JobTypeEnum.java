package com.maxqiu.demo.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务类型枚举
 *
 * @author Max_Qiu
 */
@Getter
@AllArgsConstructor
public enum JobTypeEnum {
    SIMPLE(1, "一般任务"),

    CRON(2, "定时任务"),

    ;

    @JsonValue
    private final Integer code;
    private final String msg;
}
