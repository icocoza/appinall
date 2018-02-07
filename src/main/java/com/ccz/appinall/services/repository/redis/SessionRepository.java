package com.ccz.appinall.services.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.ccz.appinall.services.model.redis.SessionInfo;

@Repository
public class SessionRepository {
	
	@Autowired
    private RedisTemplate<String, SessionInfo> redisSessionTemplate;
	
	private final String KEY = "chsvc:user:";
	
	public void save(String userId, SessionInfo sessionInfo) {
		redisSessionTemplate.opsForValue().set(KEY + userId, sessionInfo);
	}

	public void save(String userId, String ip) {
		redisSessionTemplate.opsForValue().set(KEY + userId, new SessionInfo(userId, ip));
	}

	public SessionInfo get(String userId) {
		return redisSessionTemplate.opsForValue().get(KEY + userId);
	}
	public String getIp(String userId) {
		SessionInfo userIp = redisSessionTemplate.opsForValue().get(KEY + userId);
		if(null != userIp) {
			return userIp.getIp();
		}
		return null;
	}
	
	public boolean exist(String userId) {
		return redisSessionTemplate.hasKey(KEY+userId);
	}
	
	//[TODO] 네트웍 연결이 끊어졌다가 바로 재접속 하는 사용자의 경우, 첫번째 CLOSE 메시지가 두번째 CONN 보다 늦게 들어올 수 있음. 예외 처리 필요 (중요)
	public boolean delete(String userId, SessionInfo userSession) {
		SessionInfo savedUserSession = this.get(userId);
		if(savedUserSession!=null && savedUserSession.equals(userSession)) {
			redisSessionTemplate.delete(KEY + userId);
			return true;
		}
		return false;
	}

}

