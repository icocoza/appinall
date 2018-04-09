package com.ccz.appinall.services.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.ccz.appinall.services.model.redis.SessionData;

@Repository
public class SessionRepository {
	
	@Autowired
    private RedisTemplate<String, SessionData> redisSessionTemplate;
	
	private final String KEY = "server:user:";
	
	public SessionData save(String userId, String ip) {
		SessionData sd = new SessionData(userId, ip);
		redisSessionTemplate.opsForValue().set(KEY + userId, sd);
		return sd;
	}

	public SessionData get(String userId) {
		return redisSessionTemplate.opsForValue().get(KEY + userId);
	}
	
	public String getIp(String userId) {
		SessionData userIp = redisSessionTemplate.opsForValue().get(KEY + userId);
		if(null != userIp) {
			return userIp.getIp();
		}
		return null;
	}
	
	public boolean exist(String userId) {
		return redisSessionTemplate.hasKey(KEY+userId);
	}
	
	//[TODO] 네트웍 연결이 불안한 사용자의 경, 첫번째 CLOSE가 두번째 CONNECTION 보다 늦을 수 있음.
	public boolean delete(String userId, SessionData userSession) {
		SessionData savedUserSession = this.get(userId);
		if(savedUserSession!=null && savedUserSession.equals(userSession)) {
			redisSessionTemplate.delete(KEY + userId);
			return true;
		}
		return false;
	}

}

