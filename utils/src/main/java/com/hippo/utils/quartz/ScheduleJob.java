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


  private String jobName;

  private String jobGroup;

  private String cronExpression;

  private String className;

  private String paramJson;



  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getParamJson() {
    return paramJson;
  }

  public void setParamJson(String paramJson) {
    this.paramJson = paramJson;
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


}
