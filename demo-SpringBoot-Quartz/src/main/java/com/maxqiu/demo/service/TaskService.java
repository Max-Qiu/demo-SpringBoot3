package com.maxqiu.demo.service;

import com.maxqiu.demo.enums.JobTypeEnum;
import com.maxqiu.demo.pojo.TaskInfoVO;
import com.maxqiu.demo.request.CreateCronJobFormRequest;
import com.maxqiu.demo.request.CreateSimpleJobFormRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 任务服务类
 *
 * @author Max_Qiu
 */
@Slf4j
@Service
public class TaskService {
    @Resource
    private Scheduler scheduler;

    /**
     * 获取任务列表
     */
    public List<TaskInfoVO> getJobList() throws SchedulerException {
        List<TaskInfoVO> list = new ArrayList<>();
        for (String jobGroupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals(jobGroupName))) {
                for (Trigger trigger : scheduler.getTriggersOfJob(jobKey)) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                    TaskInfoVO vo = new TaskInfoVO();
                    vo.setJobName(jobKey.getName());
                    vo.setJobGroupName(jobKey.getGroup());
                    vo.setJobDescription(jobDetail.getDescription());
                    vo.setJobClassName(jobDetail.getJobClass().getName());
                    vo.setJobStatus(triggerState.name());
                    if (trigger.getStartTime() != null) {
                        vo.setStartTime(trigger.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                    if (trigger.getEndTime() != null) {
                        vo.setEndTime(trigger.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                    vo.setData(trigger.getJobDataMap());
                    if (trigger instanceof CronTrigger cronTrigger) {
                        vo.setJobType(JobTypeEnum.CRON);
                        vo.setCron(cronTrigger.getCronExpression());
                    } else if (trigger instanceof SimpleTrigger simpleTrigger) {
                        vo.setJobType(JobTypeEnum.SIMPLE);
                        vo.setRepeatCount(simpleTrigger.getRepeatCount());
                        vo.setRepeatInterval(simpleTrigger.getRepeatInterval());
                    }
                    list.add(vo);
                }
            }
        }
        return list;
    }

    /**
     * 创建定时任务
     */
    public boolean createCronJob(CreateCronJobFormRequest formRequest) {
        JobDataMap dataMap = new JobDataMap();
        if (formRequest.getData() != null) {
            dataMap.putAll(formRequest.getData());
        }
        Class<? extends Job> clazz;
        try {
            // noinspection unchecked
            clazz = (Class<? extends Job>) Class.forName(formRequest.getJobClassName());
            return createCronJob(formRequest.getJobName(), Scheduler.DEFAULT_GROUP, formRequest.getJobDescription(), clazz, formRequest.getCron(),
                    formRequest.getStartTime(), formRequest.getEndTime(), dataMap);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查任务是否存在
     */
    public boolean checkExists(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            return scheduler.checkExists(triggerKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建定时任务
     */
    public boolean createCronJob(String jobName, String group, String jobDescription, Class<? extends Job> clazz, String cron,
                                 LocalDateTime startTime, LocalDateTime endTime, JobDataMap data) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, group);
        JobKey jobKey = JobKey.jobKey(jobName, group);
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing();
        TriggerBuilder<Trigger> triggerTriggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey);
        if (startTime != null) {
            triggerTriggerBuilder.startAt(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
        if (endTime != null) {
            triggerTriggerBuilder.startAt(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
        CronTrigger trigger = triggerTriggerBuilder.withSchedule(cronScheduleBuilder).build();
        try {
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(jobDescription).usingJobData(data).build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("任务: {} 添加成功", jobName);
            return true;
        } catch (SchedulerException e) {
            log.error("任务创建失败", e);
            return false;
        }
    }

    /**
     * 创建一般任务
     */
    public boolean createSimpleJob(CreateSimpleJobFormRequest formRequest) {
        JobDataMap dataMap = new JobDataMap();
        if (formRequest.getData() != null) {
            dataMap.putAll(formRequest.getData());
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(formRequest.getJobName(), Scheduler.DEFAULT_GROUP);
        JobKey jobKey = JobKey.jobKey(formRequest.getJobName(), Scheduler.DEFAULT_GROUP);
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(formRequest.getRepeatInterval()).withRepeatCount(formRequest.getRepeatCount());
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                // 开始时间
                .startAt(Date.from(formRequest.getStartTime().atZone(ZoneId.systemDefault()).toInstant()))
                // 结束时间
                .endAt(Date.from(formRequest.getEndTime().atZone(ZoneId.systemDefault()).toInstant())).withSchedule(simpleScheduleBuilder).build();

        Class<? extends Job> clazz;
        try {
            // noinspection unchecked
            clazz = (Class<? extends Job>) Class.forName(formRequest.getJobClassName());
            JobDetail jobDetail =
                    JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(formRequest.getJobDescription()).usingJobData(dataMap).build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("任务: {} 添加成功", formRequest.getJobName());
            return true;
        } catch (ClassNotFoundException | SchedulerException e) {
            log.error("任务创建失败", e);
            return false;
        }
    }

    /**
     * 更新定时任务
     */
    public boolean updateCronJob(CreateCronJobFormRequest formRequest) {
        JobDataMap dataMap = new JobDataMap();
        if (formRequest.getData() != null) {
            dataMap.putAll(formRequest.getData());
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(formRequest.getJobName(), Scheduler.DEFAULT_GROUP);
        JobKey jobKey = new JobKey(formRequest.getJobName(), Scheduler.DEFAULT_GROUP);
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(formRequest.getCron()).withMisfireHandlingInstructionDoNothing();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
        JobDetail jobDetail;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
            jobDetail = jobDetail.getJobBuilder().withDescription(formRequest.getJobDescription()).usingJobData(dataMap).build();
            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(cronTrigger);
            scheduler.scheduleJob(jobDetail, triggerSet, true);
            return true;
        } catch (SchedulerException e) {
            log.error("任务更新失败", e);
            return false;
        }
    }

    /**
     * 更新一般任务
     */
    public boolean updateSimpleJob(CreateSimpleJobFormRequest formRequest) {
        JobDataMap dataMap = new JobDataMap();
        if (formRequest.getData() != null) {
            dataMap.putAll(formRequest.getData());
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(formRequest.getJobName(), Scheduler.DEFAULT_GROUP);
        JobKey jobKey = new JobKey(formRequest.getJobName(), Scheduler.DEFAULT_GROUP);
        SimpleTrigger trigger =
                TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .startAt(Date.from(formRequest.getStartTime().atZone(ZoneId.systemDefault()).toInstant()))
                        .endAt(Date.from(formRequest.getEndTime().atZone(ZoneId.systemDefault()).toInstant())).withSchedule(SimpleScheduleBuilder
                                .simpleSchedule().withIntervalInMilliseconds(formRequest.getRepeatInterval()).withRepeatCount(formRequest.getRepeatCount()))
                        .build();
        JobDetail jobDetail;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
            jobDetail = jobDetail.getJobBuilder().withDescription(formRequest.getJobDescription()).usingJobData(dataMap).build();
            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(trigger);
            scheduler.scheduleJob(jobDetail, triggerSet, true);
            return true;
        } catch (SchedulerException e) {
            log.error("任务更新失败", e);
            return false;
        }
    }

    /**
     * 删除任务
     */
    public boolean deleteJob(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            log.info("任务: {} 删除成功", jobName);
            return true;
        } catch (SchedulerException e) {
            log.error("任务删除失败", e);
            return false;
        }
    }

    /**
     * 暂停任务
     */
    public boolean pauseJob(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            scheduler.pauseTrigger(triggerKey);
            log.info("任务: {} 暂停成功", jobName);
            return true;
        } catch (SchedulerException e) {
            log.error("任务暂停失败", e);
            return false;
        }
    }

    /**
     * 恢复任务
     */
    public boolean resumeJob(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            scheduler.resumeTrigger(triggerKey);
            log.info("任务: {} 恢复成功", jobName);
            return true;
        } catch (SchedulerException e) {
            log.error("任务恢复失败", e);
            return false;
        }
    }
}
