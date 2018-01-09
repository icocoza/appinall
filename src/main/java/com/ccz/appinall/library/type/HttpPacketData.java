package com.ccz.appinall.library.type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ccz.appinall.config.DefaultConfig;
import com.ccz.appinall.library.type.enums.EHttpStatus;
import com.ccz.appinall.library.util.HttpUtil;
import com.ccz.appinall.library.util.QueuedBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;

public class HttpPacketData extends QueuedBuffer {
	
	public String method, uri, version;
	public long   contentLength;
	public String contentType, contentBoundary;
	public String action, command; 	//user defined header
	
	public Map<String, String> params = new HashMap<>();
	public Map<String, String> httpHeaders = new HashMap<>();
	
	private String headerData;
	private HttpMultipartData multipartData;
	private String uploadDir;
	
	public HttpPacketData(ChannelHandlerContext ctx, String uploadDir) {
		super(ctx);
		this.uploadDir = uploadDir;
	}
	
	public boolean hasHttpHeader(int maxLen) {	//maxLen = 2048
		byte[] bytes = super.getBytes(0, maxLen);
		int pos = HttpUtil.httpHeaderSplitPos(bytes, 0, bytes.length);
		if(pos < 0)
			return false;
		headerData = new String(super.read(pos));
		return true;
	}
	
	public boolean isDecodeHeader() {
		return method != null;
	}
	
	public boolean isPostMethod() {
		return "POST".equals(method.toUpperCase());
	}
	
    public boolean hasPostData()	// not for multipart
    {
	    	if(multipartData != null)
	    		return multipartData.isDoneMultipart();
	    	else
	    		return super.size() >= contentLength;
    }

    public boolean isMultipart() {
    	if(contentType == null)
    		return false;
    	return "multipart/form-data".contains(contentType); 
    }
    
    public long getFilesize() {
    	if(multipartData!=null)
    		return multipartData.getFilesize();
    	return -1;
    }
    
    public String getFilePath() {
    	if(multipartData!=null)
    		return multipartData.getFilePath();
    	return null;
    }
    
    public boolean isKeepAlive() {
    	if(httpHeaders.containsKey("connection")==false)
    		return false;
    	return httpHeaders.get("connection").equals("keep-alive");
    }
    
    @Override
	public void write(ByteBuf buf) { 
    	if(multipartData==null)
    		super.write(buf);
    	else
    		multipartData.write(buf);
	}
    
	public EHttpStatus decodeHttpHeader() {
		String[] headerLines = headerData.split("\r\n");
		
        String[] split = headerLines[0].split(" ");	//1st line
        if (split.length < 3)
            return EHttpStatus.eBadRequest;
        method = split[0];
        uri = split[1];
        version = split[2]; 
        
        if(decodeParams(uri)==false)
        	uri = HttpUtil.decodePercent(uri);
        for(int i=1; i<headerLines.length; i++)
        {
            String line = headerLines[i].toLowerCase();
            int pos = line.indexOf(':');
            if(pos > 0)
                httpHeaders.put(line.substring(0, pos).trim(), line.substring(pos + 1).trim());
        }
        contentLength = this.getContentLength();
        contentType = this.getContentType();
        contentBoundary = this.getBoundary();
        
        if(httpHeaders.containsKey("action"))
        		action = httpHeaders.get("action");
        if(httpHeaders.containsKey("command"))
        		command = httpHeaders.get("command");

        if(contentLength < 0)
        		return EHttpStatus.eLengthRequired;
        else if(contentType == null)
        		return EHttpStatus.eBadRequest;
        else if(isMultipart()==true && contentBoundary==null)
        		return EHttpStatus.eUser_NoBoundary;
        
        if(this.isMultipart())
        		multipartData = new HttpMultipartData(this, uploadDir);
        
        return EHttpStatus.eOK;
	}
	
    private boolean decodeParams(String uri)
    {
    	if(uri.indexOf('?') < 0)
    		return false;
        String[] split = uri.split("&");
        for (String str : split)
        {
            int pos = str.indexOf('=');
            if (pos < 0)
                continue;
            String key = HttpUtil.decodePercent(str.substring(0, pos));
            String value = HttpUtil.decodePercent(str.substring(0, pos+1));
            params.put(key, value);
        }
        return true;
    }
	
    private long getContentLength()
    {
    	if(httpHeaders.containsKey("content-length")==false)
    		return -1L;
        String clength = httpHeaders.get("content-length");
        try
        {
        	return Long.parseLong(clength);
        }
        catch(Exception e){
			return -1L;
		}
    }

    private String getContentType()
    {
    	if(httpHeaders.containsKey("content-type")==false)
    		return null;
    	String ctype = httpHeaders.get("content-type");
    	String[] split = ctype.split(";");
    	if(split.length > 0)
    		return split[0].trim();
    	return null;
    }  
    
    private String getBoundary() {
    	if(httpHeaders.containsKey("content-type")==false)
    		return null;
    	String ctype = httpHeaders.get("content-type");
    	String[] split = ctype.split(";");
    	if(split.length < 2)
    		return null;
    	split = split[1].trim().split("=");
        if (split.length < 2)
            return null;
        return split[1];
    }
    
	class HttpMultipartData {
		private HttpPacketData httpdata;
		public Map<String, String> multipartHeaders = new HashMap<>();
		private long multipartSize = 0;
		
		private String multipartHeaderData;
		private long multipartFileSize = 0, writeFileSize=0;
		private FileOutputStream multipartFileStream = null;
		private String uploadDir;
		
		public HttpMultipartData(HttpPacketData httpdata, String uploadDir) {
			this.httpdata = httpdata;
			this.uploadDir = uploadDir;
			init();
			prepareMultipart();
		}
		
		private void init() {
			File uploadFileDir = new File(uploadDir);
			if(uploadFileDir.exists()==false)
				uploadFileDir.mkdirs();
		}
		
		private void prepareMultipart() {
			int headerPos = 0;
			if( (headerPos = hasMultipartHeader(2048)) == 0 )
				return;
			
			String filename = getFilePath();
			if(filename==null)	return;
			
			String filePath = uploadDir + File.separator + filename;
			try {
				multipartFileSize = contentLength - headerPos - contentBoundary.length() - 8; //multipart header size + last boundary size + 4 (means two dash -- + \r\n)
				multipartFileStream = new FileOutputStream(filePath);
				byte[] data = httpdata.read((int)multipartFileSize);
				multipartFileStream.write(data);
				multipartFileStream.flush();
				writeFileSize += data.length;
				if(multipartFileSize <= writeFileSize)
					multipartFileStream.close();
				
				multipartSize = headerPos + data.length;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void write(ByteBuf buf) { 
    		try {
	    		byte[] data =new byte[buf.writerIndex()];
	    		buf.readBytes(data);
	    		
	    		int dataSize = data.length;
	    		if(dataSize + writeFileSize > multipartFileSize)
	    			dataSize = (int) (multipartFileSize - writeFileSize);
				multipartFileStream.write(data, 0, dataSize);
				this.writeFileSize += dataSize;
				
				if(this.writeFileSize == multipartFileSize) 
					multipartFileStream.close();
				multipartSize += data.length;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public boolean isDoneMultipart() {
			return contentLength <= multipartSize;
		}
		
		public long getFilesize() {
			return multipartFileSize;
		}
		
		private int hasMultipartHeader(int maxLen) {	//maxLen = 2048
			byte[] bytes = httpdata.getBytes(0, maxLen);
			int pos = HttpUtil.httpHeaderSplitPos(bytes, 0, bytes.length);
			if(pos < 0)
				return 0;
			multipartHeaderData = new String(httpdata.read(pos));
			
			String[] splitLines = multipartHeaderData.split("\r\n");
			if(splitLines[0].toLowerCase().contains(contentBoundary)==false)
				return 0;
			
			for(int i=1; i<splitLines.length; i++) {
				String line = splitLines[i];
				String[] split = line.split(":");
				if(split.length > 1)
					multipartHeaders.put(split[0].trim().toLowerCase(), split[1].trim());
			}
			return pos;
		}
		
		private String getFilePath() {
			if(multipartHeaders.containsKey(HttpHeaderNames.CONTENT_DISPOSITION.toString()) == false)
				return null;
			String contentDisposition = multipartHeaders.get(HttpHeaderNames.CONTENT_DISPOSITION.toString());
			String[] dispositions = contentDisposition.split(";");
			for(String disposition : dispositions) {
				String[] split = disposition.split("=");
				if(split[0].trim().toLowerCase().equals("filename"))
					return split[1].substring(split[1].indexOf("\"") + 1, split[1].lastIndexOf("\""));
			}
			return null;
		}
	}
	

}

