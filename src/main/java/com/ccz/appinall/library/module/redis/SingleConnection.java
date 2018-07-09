package com.ccz.appinall.library.module.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SingleConnection implements IRedisConnection {
	JedisPool jPool = null;
	
	public SingleConnection(String masters) {	//master = ip:port,ip:port
		jPool = new JedisPool(new JedisPoolConfig(), masters);
	}
	
	public Jedis getResource() {
		return jPool.getResource();
	}

}
