package com.ccz.appinall.library.datastore;

import com.ccz.appinall.library.type.HttpPacketData;
import com.ccz.appinall.library.type.enums.EDataStoreType;

public class HttpMultipart extends HttpGet {
	HttpPacketData httpdata;
	public HttpMultipart(HttpPacketData httpdata) {
		super(httpdata);
		this.httpdata = httpdata;
	}

	@Override
	public EDataStoreType dataType() {
		return EDataStoreType.multipart;
	}

	@Override
	public long size() {
		return httpdata.getFilesize();
	}

	@Override
	public String getFilePath() {
		return httpdata.getFilePath();
	}
}
