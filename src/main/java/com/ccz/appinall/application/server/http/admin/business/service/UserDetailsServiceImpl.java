package com.ccz.appinall.application.server.http.admin.business.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.entity.db.RecAdminUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		RecAdminUser adminUser = DbAppManager.getInst().getAdminUser(username);
		if(adminUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(adminUser.email, adminUser.passwd, AuthorityUtils.createAuthorityList(adminUser.userrole.getValue()));
	}

}
