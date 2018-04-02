package com.ccz.appinall.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.FileSession;

import io.netty.util.AttributeKey;

@Configuration
public class ChAttributeKey {
	
	@Bean
	public AttributeKey<IWriteProtocol> getWriteKey() {
		return AttributeKey.valueOf(IWriteProtocol.class.getSimpleName());
	}
	@Bean
	public AttributeKey<AuthSession> getAuthSessionKey() {
		return AttributeKey.valueOf(AuthSession.class.getSimpleName());
	}
	@Bean
	public AttributeKey<FileSession> getFileSessionKey() {
		return AttributeKey.valueOf(FileSession.class.getSimpleName());
	}

}
