package com.ccz.appinall.library.module.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClusterCommander {

	/*JedisCluster jcluster;
	public RedisClusterCommander(List<HostAndPort> hostAndPortList) {
		Set<HostAndPort> jedisClusterNodes = new HashSet<>();
		
		for(HostAndPort hp : hostAndPortList)
			jedisClusterNodes.add(hp);
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMinIdle(10);
		config.setMaxIdle(50);
		
		jcluster = new JedisCluster(jedisClusterNodes, 3000, 10, config); //3000 is timeout, 10 is max redirection count
	}
	
	//master commanders
	public boolean set(String key, String value) {
		return jcluster.set(key, value) != null;
	}
	
	public long sadd(String key, String[] member) {
		return jcluster.sadd(key, member);
	}

	public boolean hset(String key, String field, String value) {
		return jcluster.hset(key, field, value)>0;
	}

	public boolean hmset(String key, Map<String, String> map) {
		return jcluster.hmset(key, map) != null;	
	}	
	
	public long hincrBy(String key, String field) {
		return jcluster.hincrBy(key, field, 1L);
	}
	
	public long hincrBy(String key, String field, long incValue) {
		return jcluster.hincrBy(key, field, incValue); 
	}
	
	public boolean del(String key) {
		return jcluster.del(key) >0;
	}
	
	public boolean hdel(String key, String field) {
		return jcluster.hdel(key, field) >0;
	}
	
	public boolean hdel(String key, String[] fields) {
		for(String field : fields)
			hdel(key, field);
		return true;
	}
	
	public long srem(String key, String[] members) {
		return jcluster.srem(key, members);
	}
	
	public long hlen(String key) {
		return jcluster.hlen(key);
	}
	
	public long decr(String key) {
		return jcluster.decr(key);
	}

	public long lpush(String key, String value) {
		return jcluster.lpush(key, value);
	}
	
	public long rpush(String key, String value) {
		return jcluster.rpush(key, value);
	}
	
	public String lpop(String key) {
		return jcluster.lpop(key);
	}
	
	public String rpop(String key) {
		return jcluster.rpop(key);
	}
	
	public String brpop(int timeout, String key) {
		List<String> values = jcluster.brpop(timeout, key);
		if(values==null || values.size()<2)
			return "";			
		return values.get(1);
	}
	
	public String blpop(int timeout, String key) {
		List<String> values = jcluster.blpop(timeout, key);
		if(values==null || values.size()<2)
			return "";			
		return values.get(1);
	}
	
	//slave commanders
	public String get(String key) {
		return jcluster.get(key);
	}

	public List<String> mget(String[] keys) {
		List<String> values = new ArrayList<>();
		for(String key : keys)
			values.add(jcluster.get(key));
		return values;
	}	

	public String hget(String key, String field) {
		return jcluster.hget(key, field);
	}

	public List<String> hmget(String key, String[] fields) {
		return jcluster.hmget(key, fields);	
	}
	
	public Map<String, String> hgetAll(String key) {
		return jcluster.hgetAll(key);	
	}

	public boolean exists(String key) {
		return jcluster.exists(key);
	}
	
	public boolean hexists(String key, String field) {
		return jcluster.hexists(key, field);
	}
	
	public String[] smembers(String key) {
		Set<String> members = jcluster.smembers(key);
		return members.toArray(new String[members.size()]);
	}
	
	public boolean sismember(String key, String member) {
		return jcluster.sismember(key, member);
	}

	public long scard(String key) {	//return count
		return jcluster.scard(key);	
	}	
	
	public Set<String> keys(String pattern) {
		TreeSet<String> keys = new TreeSet<>();
		Map<String, JedisPool> clusterNodes = jcluster.getClusterNodes();
		
		for(String key : clusterNodes.keySet()){
			JedisPool jp = clusterNodes.get(key);
			Jedis jedis = jp.getResource();
			keys.addAll(jedis.keys(pattern));
			jedis.close();
		}
		return keys;
	}*/
}
