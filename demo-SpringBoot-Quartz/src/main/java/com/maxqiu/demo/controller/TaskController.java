package com.maxqiu.demo.controller;

import com.maxqiu.demo.pojo.Result;
import com.maxqiu.demo.pojo.TaskInfoVO;
import com.maxqiu.demo.request.CreateCronJobFormRequest;
import com.maxqiu.demo.request.CreateSimpleJobFormRequest;
import com.maxqiu.demo.service.TaskService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务 前端控制器
 *
 * @author Max_Qiu
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    @Resource
    private TaskService taskService;

    /**
     * 获取任务列表
     */
    @GetMapping(value = "/list")
    public Result<List<TaskInfoVO>> getJobList() {
        try {
            List<TaskInfoVO> jobList = taskService.getJobList();
            return Result.success(jobList);
        } catch (SchedulerException e) {
            return Result.fail();
        }
    }

    /**
     * 添加定时任务
     */
    @PostMapping("/create/cron")
    public Result<Object> createCronJob(@RequestBody @Valid CreateCronJobFormRequest formRequest) {
        if (taskService.checkExists(formRequest.getJobName(), Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务已存在");
        }
        return Result.byFlag(taskService.createCronJob(formRequest));
    }

    /**
     * 添加一般任务
     */
    @PostMapping("/create/simple")
    public Result<Object> createSimpleJob(@RequestBody @Valid CreateSimpleJobFormRequest formRequest) {
        if (taskService.checkExists(formRequest.getJobName(), Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务已存在");
        }
        return Result.byFlag(taskService.createSimpleJob(formRequest));
    }

    /**
     * 更新定时任务
     */
    @PostMapping("/update/cron")
    public Result<Object> updateCronJob(@RequestBody @Valid CreateCronJobFormRequest formRequest) {
        if (!taskService.checkExists(formRequest.getJobName(), Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务不存在");
        }
        return Result.byFlag(taskService.updateCronJob(formRequest));
    }

    /**
     * 更新一般任务
     */
    @PostMapping("/update/simple")
    public Result<Object> updateSimpleJob(@RequestBody @Valid CreateSimpleJobFormRequest formRequest) {
        if (!taskService.checkExists(formRequest.getJobName(), Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务不存在");
        }
        return Result.byFlag(taskService.updateSimpleJob(formRequest));
    }

    /**
     * 删除任务
     */
    @PostMapping("/delete/{jobName}")
    public Result<Object> deleteJob(@PathVariable String jobName) {
        if (!taskService.checkExists(jobName, Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务不存在");
        }
        return Result.byFlag(taskService.deleteJob(jobName, Scheduler.DEFAULT_GROUP));
    }

    /**
     * 暂停任务
     */
    @PostMapping("/pause/{jobName}")
    public Result<Object> pauseJob(@PathVariable String jobName) {
        if (!taskService.checkExists(jobName, Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务不存在");
        }
        return Result.byFlag(taskService.pauseJob(jobName, Scheduler.DEFAULT_GROUP));
    }

    /**
     * 恢复任务
     */
    @PostMapping("/resume/{jobName}")
    public Result<Object> resumeJob(@PathVariable String jobName) {
        if (!taskService.checkExists(jobName, Scheduler.DEFAULT_GROUP)) {
            return Result.fail("任务不存在");
        }
        return Result.byFlag(taskService.resumeJob(jobName, Scheduler.DEFAULT_GROUP));
    }
}
