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
public class CreateSimpleJobFormRequest {
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
     * 重复次数
     */
    private Integer repeatCount;

    /**
     * 间隔时间（毫秒）
     */
    private Long repeatInterval;

    /**
     * 补充数据
     */
    private Map<String, Object> data;
}
