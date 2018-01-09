package com.ccz.appinall.library.type.inf;

import com.ccz.appinall.library.type.enums.EDataStoreType;

import io.netty.buffer.ByteBuf;

public interface IDataStore {
	public void write(byte[] buf, int length);
	public void write(ByteBuf buf);
	public boolean flush();
	public EDataStoreType dataType();
	public long size();
}
