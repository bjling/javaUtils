package com.hippo.utils.quartz;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:/application-*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class QuartzTest {

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  @Test
  public void jobTest() throws SchedulerException, InterruptedException {
    ScheduleJob job = new ScheduleJob();
    job.setJobName("JobName_" + Thread.currentThread().getId());
    job.setJobGroup("testGroup");
    job.setClassName("com.hippo.utils.quartz.TestLogic");
    job.setParamJson("jobId:123213");
    job.setCronExpression("0/3 * * * * ?");
    QuartzUtils.addJob(schedulerFactoryBean.getScheduler(), job);
    Thread.sleep(10 * 1000L);
    QuartzUtils.pauseJob(schedulerFactoryBean.getScheduler(), job);
    Thread.sleep(10 * 1000L);
    QuartzUtils.resumeJob(schedulerFactoryBean.getScheduler(), job);
    Thread.sleep(10 * 1000L);
    job.setCronExpression("0/5 * * * * ?");
    QuartzUtils.updCronExpression(schedulerFactoryBean.getScheduler(), job);
    Thread.sleep(10 * 1000L);
    System.out.println("over");
  }
}
