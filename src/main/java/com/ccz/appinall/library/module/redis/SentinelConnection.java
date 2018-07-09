package com.ccz.appinall.library.module.redis;

import java.util.HashSet;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class SentinelConnection { //implements IRedisConnection {

	JedisSentinelPool sentinelPool = null;
	
	public SentinelConnection(String masterName, List<String> hostAndPort) {
		sentinelPool = new JedisSentinelPool(masterName, new HashSet<String>(hostAndPort));
	}
	
	public Jedis getResource() {
		return sentinelPool.getResource();
	}
}
