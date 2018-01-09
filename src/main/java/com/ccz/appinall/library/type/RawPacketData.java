package com.ccz.appinall.library.type;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import com.ccz.appinall.library.datastore.FileDataStore;
import com.ccz.appinall.library.datastore.TextDataStore;
import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.util.QueuedBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RawPacketData extends QueuedBuffer {
	final short RAW_PACKET_HEADER_SIZE = 8;
	
	IDataAccess dataStore = null;
	long packetHeaderSize = 0;
	
	public RawPacketData(ChannelHandlerContext ctx) {
		super(ctx);
		init();
	}
	
	public RawPacketData(ChannelHandlerContext ctx, String filepath) throws FileNotFoundException {
		super(ctx);
		dataStore = new FileDataStore(filepath);
	}
	
	public short getHeaderChunkSize() {	return RAW_PACKET_HEADER_SIZE;	}
	
	public long  getHeaderSize() {	
		if(super.size() < RAW_PACKET_HEADER_SIZE)
			return -1;
		if(packetHeaderSize > 0)
			return packetHeaderSize;
		ByteBuffer bybuf = super.readByteBuffer(RAW_PACKET_HEADER_SIZE);
		packetHeaderSize = bybuf.getLong();
		super.discardBuf(RAW_PACKET_HEADER_SIZE);
		return packetHeaderSize;
	}
	
	public IDataAccess getDonePacket() {
		if(getHeaderSize() < 1)
			return null;
		IDataAccess refStore = null;
		if(dataStore.dataType()==EDataStoreType.file && dataStore.size()==packetHeaderSize) {
			dataStore.flush();
			refStore = dataStore; //file must be disconnected and reconnect
		} else if(dataStore.dataType()==EDataStoreType.string && super.size()>=packetHeaderSize) {
			dataStore.write(super.read((int)packetHeaderSize), (int)packetHeaderSize);
			refStore = dataStore;
			super.discardBuf((int)packetHeaderSize);
			init();	//reset datastore
		}
		return refStore;
	}
	
	@Override
	public void write(ByteBuf buf) {
		if(dataStore.dataType()==EDataStoreType.file) {
			dataStore.write(buf);
			return;
		}
		super.write(buf);
	}
	
	public boolean isFile() {
		return dataStore.dataType() == EDataStoreType.file;
	}
	
	private void init() {
		dataStore = new TextDataStore();
		packetHeaderSize = 0;
	}
	
}
