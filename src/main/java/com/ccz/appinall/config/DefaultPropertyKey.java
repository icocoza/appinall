package com.ccz.appinall.config;

import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;

import io.netty.util.AttributeKey;

public class DefaultPropertyKey {
	public static final AttributeKey<IWriteProtocol> writePropertyKey = AttributeKey.valueOf(IWriteProtocol.class.getSimpleName());
}
