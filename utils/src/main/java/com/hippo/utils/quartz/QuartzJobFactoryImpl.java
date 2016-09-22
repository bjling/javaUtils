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

    try {
      System.out
          .println("任务名称 = [" + scheduleJob.getJobName() + "],time=" + new Date().toLocaleString());
      LoginProcess loginProcess =
          (LoginProcess) Class.forName(scheduleJob.getClassName()).newInstance();
      loginProcess.execute(scheduleJob.getParamJson());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
