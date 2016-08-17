package com.hippo.utils.redis;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub{

  @Override
  public void onMessage(String channel, String message) {
    super.onMessage(channel, message);
    System.out.println(channel+"..."+message);
  }


}
