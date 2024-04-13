package com.maxqiu.demo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 创建定时任务表单
 *
 * @author Max_Qiu
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateCronJobFormRequest {
    /**
     * 任务名称
     */
    @NotBlank
    private String jobName;

    /**
     * 任务描述
     */
    @NotBlank
    private String jobDescription;

    /**
     * 任务类名
     */
    @NotBlank
    private String jobClassName;

    /**
     * cron定时任务表达式
     */
    @NotBlank
    private String cron;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 补充数据
     */
    private Map<String, Object> data;
}
