package com.hippo.utils.redis;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

  /** 纳秒 */
  private static final long MILLI_NANO_CONVERSION = 1000 * 1000L;

  /** 默认超时时间（毫秒） */
  private static final long DEFAULT_TIME_OUT = 1000L;

  private ThreadLocal<String> tl = new ThreadLocal<>();

  private static final String REDISHOST = "127.0.0.1";

  private static final int PORT = 6380;

  private static final String PWD = "Redis@123";

  private static JedisPoolConfig config;

  private static JedisPool pool;

  static {
    config = new JedisPoolConfig();
    config.setMaxIdle(10);
    config.setMaxWaitMillis(1000);
    config.setMaxTotal(30);
    pool = new JedisPool(config, REDISHOST, PORT, 2000, PWD);
  }

  /**
   * 加锁
   * 
   * @param key
   * @param timeout 单位毫秒
   * @return
   */
  public boolean lock(String key, long timeout) {
    if (StringUtils.isBlank(key) || timeout < 0) {
      throw new IllegalArgumentException("lock params is not right");
    }
    try {
      long temp;
      long nano = System.nanoTime();
      int expire = (int) (timeout / 1000 + 1);
      while ((temp = (timeout * MILLI_NANO_CONVERSION) - (System.nanoTime() - nano)) > 0) {
        if (this.setnx(key, String.valueOf(System.nanoTime())) == 1) {
          Jedis jedis = getJedis();
          jedis.expire(key, expire);
          tl.set(key);
          return true;
        }
        String s = get(key);
        if (s == null) {
          continue;
        }
        TimeUnit.NANOSECONDS.sleep(temp / 2);
        if ((System.nanoTime() - Long.parseLong(s)) / MILLI_NANO_CONVERSION / 1000 >= expire) {
          this.del(key);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("lock error", e);
    }
    return false;
  }

  public long setnx(String key, String value) {
    Jedis jedis = getJedis();
    try {
      return jedis.setnx(key, value);
    } catch (Exception e) {
      LOGGER.error("redisService setnx error", e);
    } finally {
      jedis.close();
    }
    return 0L;
  }

  public void expire(final String key, final int seconds) {
    Jedis jedis = getJedis();
    try {
      jedis.expire(key, seconds);
    } catch (Exception e) {
      LOGGER.error("redisService expire error", e);
    } finally {
      jedis.close();
    }
  }

  /**
   * 默认锁1秒
   * 
   * @param key
   * @return
   */
  public boolean lock(String key) {
    return lock(key, DEFAULT_TIME_OUT);
  }

  /**
   * 解锁
   */
  public void unlock() {
    try {
      if (this.del(tl.get()) == 1) {
        this.del(tl.get());
        tl.remove();
      }
    } catch (Exception e) {
      LOGGER.error("redisService unlock error!", e);
    }
  }

  /**
   * 获取redis value (String)
   * 
   * @param key
   * @return
   */
  public String get(String key) {
    Jedis jedis = getJedis();
    try {

      return jedis.get(key);
    } catch (Exception e) {
      LOGGER.error("redisService get error", e);
    } finally {
      jedis.close();
    }
    return null;
  }


  public long del(String key) {
    Jedis jedis = getJedis();
    try {
      return jedis.del(key);
    } catch (Exception e) {
      LOGGER.error("redisService del error", e);
    } finally {
      jedis.close();
    }
    return 0L;
  }

  /**
   * 获取一个jedis 客户端
   * 
   * @return
   */
  private Jedis getJedis() {
    return pool.getResource();
  }
}
