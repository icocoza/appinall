package com.ccz.appinall.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccz.appinall.services.model.redis.SessionData;
import com.ccz.appinall.services.repository.redis.SessionRepository;

@Service
public class SessionService {

	@Autowired
	SessionRepository sessionRepository;
	
	public void addUserSession(String userid, String ip) {
		sessionRepository.save(userid, ip);		
	}

	public void deleteUserSession(String userid, SessionData sd) {
		sessionRepository.delete(userid, sd);
	}

	public SessionData getUserSession(String userid) {
		return sessionRepository.get(userid);
	}
	
}
