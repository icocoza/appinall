package com.ccz.appinall.services.controller.file;

import java.io.IOException;

import com.ccz.appinall.library.module.scrap.ImageResizeWorker;
import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.services.model.db.RecFile;

import io.netty.channel.Channel;
import lombok.Getter;

public class FileSession extends SessionItem<UploadFile> {
	@Getter String scode;
	
	public FileSession(Channel ch, int methodType) {
		super(ch, methodType);
	}

	@Override
	public String getKey() {
		return super.item.getFile().fileid;
	}

	@Override
	public FileSession putSession(UploadFile t, String scode) {
		super.item = t;
		this.scode = scode;
		return this;
	}
	
	public void write(byte[] buf) throws IOException {
		super.item.write(buf, buf.length);
	}
	
	public boolean isOverSize() { return item.isOverSize(); }
	
	public void commit(ImageResizeWorker imageResizeWorker) throws IOException {
		super.item.commit(imageResizeWorker);
	}
	
	public void discard() throws IOException {
		super.item.discard();
	}
}
