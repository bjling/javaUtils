package com.hippo.utils.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 * 任务JOB
 * 
 * @author sl
 *
 */
public final class QuartzUtils {

  private static CronTrigger getCronTrigger(Scheduler scheduler, ScheduleJob job)
      throws SchedulerException {
    return (CronTrigger) scheduler.getTrigger(getTriggerKey(job));
  }

  private static TriggerKey getTriggerKey(ScheduleJob job) throws SchedulerException {
    return TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
  }

  /**
   * 新增and启动JOB
   * 
   * @param scheduler
   * @param job
   * @throws SchedulerException
   */
  public static void addJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
    CronTrigger trigger = getCronTrigger(scheduler, job);
    if (trigger == null) {
      JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactoryImpl.class)
          .withIdentity(job.getJobName(), job.getJobGroup()).build();
      jobDetail.getJobDataMap().put("jobData", job);
      CronScheduleBuilder scheduleBuilder =
          CronScheduleBuilder.cronSchedule(job.getCronExpression());
      trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup())
          .withSchedule(scheduleBuilder).build();
      scheduler.scheduleJob(jobDetail, trigger);
    } else {
      TriggerKey triggerKey = getTriggerKey(job);
      CronScheduleBuilder scheduleBuilder =
          CronScheduleBuilder.cronSchedule(job.getCronExpression());
      trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder)
          .build();
      scheduler.rescheduleJob(triggerKey, trigger);
    }

  }

  /**
   * 暂停JOB
   * 
   * @param scheduler
   * @param job
   * @throws SchedulerException
   */
  public static void pauseJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
    scheduler.pauseJob(new JobKey(job.getJobName(), job.getJobGroup()));
  }

  /**
   * 恢复JOB
   * 
   * @param scheduler
   * @param job
   * @throws SchedulerException
   */
  public static void resumeJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
    scheduler.resumeJob(new JobKey(job.getJobName(), job.getJobGroup()));
  }

  /**
   * 删除JOB
   * 
   * @param scheduler
   * @param job
   * @throws SchedulerException
   */
  public static void deleteJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
    scheduler.deleteJob(new JobKey(job.getJobName(), job.getJobGroup()));
  }

  /**
   * 更改cron表达式重新跑JOB
   * 
   * @param scheduler
   * @param job
   * @throws SchedulerException
   */
  public static void updCronExpression(Scheduler scheduler, ScheduleJob job)
      throws SchedulerException {
    CronTrigger trigger = getCronTrigger(scheduler, job);
    TriggerKey triggerKey = getTriggerKey(job);
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
    trigger =
        trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
    scheduler.rescheduleJob(triggerKey, trigger);
  }
}
