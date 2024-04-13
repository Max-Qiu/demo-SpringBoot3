package com.maxqiu.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maxqiu.demo.enums.JobTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Max_Qiu
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaskInfoVO {
    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroupName;

    /**
     * 任务描述
     */
    private String jobDescription;

    /**
     * 任务类名
     */
    private String jobClassName;

    /**
     * 任务类型
     */
    private JobTypeEnum jobType;

    /**
     * 任务状态
     */
    private String jobStatus;

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

    /**
     * cron定时任务表达式
     */
    private String cron;

    /**
     * 重复次数
     */
    private Integer repeatCount;

    /**
     * 间隔时间（毫秒）
     */
    private Long repeatInterval;
}
