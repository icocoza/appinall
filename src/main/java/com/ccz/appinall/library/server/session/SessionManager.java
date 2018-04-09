package com.ccz.appinall.library.server.session;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ccz.appinall.library.dbhelper.DbConnMgr;

@Component
@SuppressWarnings("rawtypes")
public class SessionManager {
	
	Map<String, SessionItem> ssMap = new ConcurrentHashMap<>();
	
//	private static SessionManager s_pThis = null;
//	public static synchronized SessionManager getInst() {
//		return s_pThis = (s_pThis == null? new SessionManager() : s_pThis);
//	}

	public SessionItem put(SessionItem si) {
		SessionItem previousItem = ssMap.put(si.getKey(), si);
		return previousItem;
	}
	
	public SessionItem get(String key) {
		return ssMap.get(key);
	}
	
	public boolean del(String key) {
		return ssMap.remove(key) != null;
	}
	
	public void clear() {
		ssMap.clear();
	}
	
	public int count() {
		return ssMap.size();
	}
	
	public Set<String> getAllKey() {
		return ssMap.keySet();
	}
	
	public Collection<SessionItem> getAllValue() {
		return ssMap.values();
	}
}
