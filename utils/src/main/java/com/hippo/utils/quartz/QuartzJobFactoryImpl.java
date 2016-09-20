package com.hippo.utils.quartz;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class QuartzJobFactoryImpl implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("jobData");
    System.out
        .println("任务名称 = [" + scheduleJob.getJobName() + "],time=" + new Date().toLocaleString());
  }
}
