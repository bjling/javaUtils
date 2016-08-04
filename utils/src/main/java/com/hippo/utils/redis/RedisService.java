package com.hippo.utils.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;


@Service
public class RedisService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

  /** 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒 */
  private static final long MILLI_NANO_CONVERSION = 1000 * 1000L;

  /** 默认超时时间（秒） */
  private static final int DEFAULT_TIME_OUT = 1;


  @Autowired(required = false)
  @Qualifier("jedisConnectionFactory")
  private JedisConnectionFactory jedisConnectionFactory;

  /**
   * 加锁
   * 
   * @param key
   * @param timeout 单位秒
   * @return
   */
  public RedisLock lock(String key, int timeout) {

    if (StringUtils.isBlank(key) || timeout < 0) {
      throw new IllegalArgumentException("lock params is not right");
    }
    long newTimeout = timeout * 1000;
    try {
      long temp;
      long nano = System.nanoTime();
      int expire = timeout + 1;
      while ((temp = (newTimeout * MILLI_NANO_CONVERSION) - (System.nanoTime() - nano)) > 0) {
        if (this.setnx(key, String.valueOf(System.nanoTime())) == 1) {
          RedisLock redisLock = new RedisLock(key);
          this.expire(key, expire);
          return redisLock;
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
    return null;
  }

  public void setex(final String key, final int seconds, final String value) {
    Jedis jedis = getJedis();
    try {
      jedis.setex(key, seconds, value);
    } catch (Exception e) {
      LOGGER.error("redisService setex error", e);
    } finally {
      jedis.close();
    }
  }

  public void setCollection(final String key, final Collection<String> collection) {
    Jedis jedis = getJedis();
    try {
      String[] strings = new String[collection.size()];
      collection.toArray(strings);
      jedis.lpush(key, collection.toArray(strings));
    } catch (Exception e) {
      LOGGER.error("redisService setCollection error", e);
    } finally {
      jedis.close();

    }
  }

  public void setCollection(final String key, final String value) {
    Jedis jedis = getJedis();
    try {
      jedis.lpush(key, value);
    } catch (Exception e) {
      LOGGER.error("redisService setCollection error", e);
    } finally {
      jedis.close();
    }
  }

  public void setCollection(final String key, final String value, final int expireSecond) {
    Jedis jedis = getJedis();
    try {
      jedis.lpush(key, value);
      jedis.expire(key, expireSecond);
    } catch (Exception e) {
      LOGGER.error("redisService setCollection expire error", e);
    } finally {
      jedis.close();

    }
  }

  public Collection<String> getCollection(final String key) {
    Jedis jedis = getJedis();
    try {
      return jedis.lrange(key, 0, getLength(key));
    } catch (Exception e) {
      LOGGER.error("redisService getCollection error", e);
    } finally {
      jedis.close();
    }
    return Collections.emptyList();
  }

  public String lpop(final String key) {
    Jedis jedis = getJedis();
    try {
      String result = jedis.lpop(key);
      if ("nil".equals(result)) {
        return null;
      } else {
        return result;
      }
    } catch (Exception e) {
      LOGGER.error("redisService lpop error", e);
    } finally {
      jedis.close();
    }
    return null;
  }

  public long getLength(final String key) {
    Jedis jedis = getJedis();
    try {
      return jedis.llen(key);
    } catch (Exception e) {
      LOGGER.error("redisService setex error", e);
    } finally {
      jedis.close();

    }
    return -1;
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
  public RedisLock lock(String key) {
    return lock(key, DEFAULT_TIME_OUT);
  }

  /**
   * 解锁
   */
  public void unlock(RedisLock redisLock) {
    try {
      if (redisLock != null) {
        this.del(redisLock.getKey());
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

  public byte[] get(byte[] key) {
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

  public long del(byte[] key) {
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

  public long setnx(byte[] key, byte[] value) {
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

  public String set(byte[] key, byte[] value) {
    Jedis jedis = getJedis();
    try {
      return jedis.set(key, value);
    } catch (Exception e) {
      LOGGER.error("redisService set error", e);
    } finally {
      jedis.close();
    }
    return null;
  }

  public String set(String key, String value) {
    Jedis jedis = getJedis();
    try {
      return jedis.set(key, value);
    } catch (Exception e) {
      LOGGER.error("redisService set error", e);
    } finally {
      jedis.close();
    }
    return null;
  }

  public Long incr(String key) {
    Jedis jedis = getJedis();
    try {
      return jedis.incr(key);
    } catch (Exception e) {
      LOGGER.error("redisService incr error", e);
    } finally {
      jedis.close();
    }
    return null;
  }

  public Long incr(byte[] key) {
    Jedis jedis = getJedis();
    try {
      return jedis.incr(key);
    } catch (Exception e) {
      LOGGER.error("redisService incr error", e);
    } finally {
      jedis.close();
    }
    return null;
  }

  /**
   * 获取一个jedis 客户端
   * 
   * @return
   */
  private Jedis getJedis() {
    return this.jedisConnectionFactory.getConnection().getNativeConnection();
  }
}


class RedisLock {
  private String key;

  public RedisLock(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

}
