package com.hippo.utils.quartz;

import java.io.Serializable;

/**
 * 任务JOB
 * 
 * @author sl
 *
 */
public class ScheduleJob implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -4413184861297738625L;

  private String jobId;

  private String jobName;

  private String jobGroup;

  private String cronExpression;

  private String desc;

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getJobGroup() {
    return jobGroup;
  }

  public void setJobGroup(String jobGroup) {
    this.jobGroup = jobGroup;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }


}
