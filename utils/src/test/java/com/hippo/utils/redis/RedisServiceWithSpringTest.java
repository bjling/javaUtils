package com.hippo.utils.redis;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:/application-*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisServiceWithSpringTest {

  @Autowired
  private RedisServiceWithSpring redisService;

  @Test
  public void lockTest() throws InterruptedException {

    redisService.del("lockTest");
    new Thread(new RedisWithSpringRunnable(redisService)).start();
    new Thread(new RedisWithSpringRunnable(redisService)).start();
    new Thread(new RedisWithSpringRunnable(redisService)).start();
    Thread.sleep(40 * 1000L);
  }
}


class RedisWithSpringRunnable implements Runnable {

  public RedisServiceWithSpring redisService;

  public RedisWithSpringRunnable(RedisServiceWithSpring redisService) {
    this.redisService = redisService;
  }

  @Override
  public void run() {
    try {
      if (redisService.lock("lockTestWithSpring", 10 * 1000L)) {
        System.out.println(Thread.currentThread().getId());
        TimeUnit.SECONDS.sleep(8);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      redisService.unlock();
    }
  }
}
