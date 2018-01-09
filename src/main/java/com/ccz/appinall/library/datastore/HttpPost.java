package com.ccz.appinall.library.datastore;

import com.ccz.appinall.library.type.HttpPacketData;
import com.ccz.appinall.library.type.enums.EDataStoreType;

public class HttpPost extends HttpGet {
	String postData;
	
	public HttpPost(HttpPacketData httpdata) {
		super(httpdata);
		postData = new String(httpdata.readAll());
	}

	@Override
	public EDataStoreType dataType() {
		return EDataStoreType.httppost;
	}

	@Override
	public long size() {
		return contentLength;
	}

	@Override
	public boolean isJson() {
		return postData.startsWith("{");
	}

	@Override
	public String getStringData() {
		return postData;
	}
}
