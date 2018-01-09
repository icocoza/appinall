package com.ccz.appinall.library.module.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class RedisCommander {	//standalone redis connection or sentinel type
	/*IRedisConnection connector;
	
	public RedisCommander(IRedisConnection connector) {
		this.connector = connector;
	}
	
	//master commanders
	public boolean set(String key, String value) {
		Jedis jedis = connector.getResource();
		try{			
			jedis.set(key, value); 
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close(); 
		} 
		return true;
	}
	
	public long sadd(String key, String[] setItem) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.sadd(key, setItem);
		}catch(JedisException e){ 
			return 0;
		}finally{ 
			if(null != jedis) jedis.close();			
		}
	}

	public boolean hset(String key, String field, String value) {
		Jedis jedis = connector.getResource();
		try{			
			jedis.hset(key, field, value);
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close();			
		} 
		return true;
	}

	public boolean hmset(String key, Map<String, String> map) {
		Jedis jedis = connector.getResource();
		try{			
			jedis.hmset(key, map);
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close();			
		} 
		return true;	
	}	
	
	public long hincrBy(String key, String field) {
		return hincrBy(key, field, 1L);
	}
	
	public long hincrBy(String key, String field, long incValue) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.hincrBy(key, field, incValue);
		}catch(JedisException e){ 
			return 0L;
		}finally{ 
			if(null != jedis) jedis.close();			
		} 
	}
	
	public boolean del(String key) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.del(key)>0;
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close(); 
		} 
	}
	
	public boolean hdel(String key, String field) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.hdel(key, field)>0;
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close(); 
		} 
	}
	
	public boolean hdel(String key, String[] fields) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.hdel(key, fields)>0;
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close(); 
		} 
	}
	
	public long srem(String key, String[] setItem) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.srem(key, setItem);
		}catch(JedisException e){ 
			return 0;
		}finally{ 
			if(null != jedis) jedis.close();			
		}
	}
	
	public long hlen(String key) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.hlen(key);
		}catch(JedisException e){ 
			return 0;	
		}finally{ 
			if(null != jedis) jedis.close();			
		} 
	}
	
	public long decr(String key) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.decr(key);
		}catch(JedisException e){ 
			return 0;
		}finally{ 
			if(null != jedis) jedis.close();			
		}
	}

	public long lpush(String key, String data) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.lpush(key, data);
		}catch(JedisException e){ 
			return 0;
		}finally{ 
			if(null != jedis) jedis.close();			
		}
	}
	
	public long rpush(String key, String data) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.rpush(key, data);
		}catch(JedisException e){ 
			return 0;
		}finally{ 
			if(null != jedis) jedis.close();			
		}
	}
	
	public String lpop(String key) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.lpop(key);
		}catch(JedisException e){ 
			return "";
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}
	
	public String rpop(String key) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.rpop(key);
		}catch(JedisException e){ 
			return "";
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}
	
	public String brpop(int timeout, String key) {
		Jedis jedis = connector.getResource();
		try{					
			List<String> values = jedis.brpop(timeout, key);
			if(values==null || values.size()<1)
				return "";
			return values.get(1);
		}catch(JedisException e){ 
			return "";
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}
	
	public String blpop(int timeout, String key) {
		Jedis jedis = connector.getResource();
		try{					
			List<String> values = jedis.blpop(timeout, key);
			if(values==null || values.size()<1)
				return "";
			return values.get(1);
		}catch(JedisException e){ 
			return "";
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}
	
	//slave commanders
	public String get(String key) {
		Jedis jedis = connector.getResource();
		if(jedis==null)
			return "";
		try{			
			return jedis.get(key);
		}catch(JedisException e){ 
			if(null != jedis){ 
				jedis.close(); 
		        jedis = null; 
			}
			return "";
		}finally{ 
			if(null != jedis)
				jedis.close();			
		}	
	}

	public List<String> mget(String[] keys) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.mget(keys);
		}catch(JedisException e){ 
			return null;
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}	

	public String hget(String key, String field) {
		Jedis jedis = connector.getResource();
		try{
			return jedis.hget(key, field);
		}catch(JedisException e){ 
			return "";
		}finally{ 
			if(null != jedis) jedis.close(); 			
		}	
	}

	public List<String> hmget(String key, String[] fields) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.hmget(key, fields);
		}catch(JedisException e){ 
			return null;
		}finally{ 
			if(null != jedis) jedis.close(); 			
		}	
	}
	
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = connector.getResource();
		try{						
			return jedis.hgetAll(key);
		}catch(JedisException e){ 
			return null;
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}

	public boolean exists(String key) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.exists(key);
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}
	
	public boolean hexists(String key, String field) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.hexists(key, field); 
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close();			
		} 
	}
	
	public String[] smembers(String key) {
		Jedis jedis = connector.getResource();
		try{			
			Set<String> members = jedis.smembers(key);
			return members.toArray(new String[members.size()]);
		}catch(JedisException e){
			return null;
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}
	
	public boolean sismember(String key, String item) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.sismember(key, item);
		}catch(JedisException e){ 
			return false;
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}

	public long scard(String key) {	//return count
		Jedis jedis = connector.getResource();
		try{			
			return jedis.scard(key);
		}catch(JedisException e){ 
			return 0L;
		}finally{ 
			if(null != jedis) jedis.close();			
		}	
	}	
	
	public Set<String> keys(String pattern) {
		Jedis jedis = connector.getResource();
		try{			
			return jedis.keys(pattern);
		}catch(JedisException e){ 
			return null;
		}finally{ 
			if(null != jedis) jedis.close();			
		}		
	}*/
}
