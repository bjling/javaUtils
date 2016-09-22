package com.hippo.utils.quartz;

public class TestLogic implements LoginProcess {

  @Override
  public void execute(String json) {
    System.out.println(json + " ...");
  }

}
