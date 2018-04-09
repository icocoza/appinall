package com.ccz.appinall.services.repository.redis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeliverChannelRepository {
	
	@Autowired
	private RedisTemplate<String, String> redisDeliverChannelOperations;
	
	private final String KEY = "aia:deliver:ch:";
	
	public void addChannelUser(String deliverId, String userId) {
		redisDeliverChannelOperations.opsForSet().add(KEY+deliverId, userId);
	}

	public Set<String> getChannelUser(String deliverId) {
		return redisDeliverChannelOperations.opsForSet().members(KEY+deliverId);
	}
	
	public void delChannelUser(String deliverId, String userId) {
		Set<String> users = redisDeliverChannelOperations.opsForSet().members(KEY+deliverId);
		for(String user : users) {
			if (userId.equals(user)) {
				redisDeliverChannelOperations.opsForSet().remove(KEY+deliverId, user);
			}
		}
		users.clear();
	}
	
	public void delChannelUser(String deliverId) {
		redisDeliverChannelOperations.delete(KEY+deliverId);
	}
}
