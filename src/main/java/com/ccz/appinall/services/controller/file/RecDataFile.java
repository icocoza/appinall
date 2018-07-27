package com.ccz.appinall.services.controller.file;

import com.ccz.appinall.services.controller.RecDataCommon;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class RecDataFile {
	@Getter
	public class FileInit extends RecDataCommon {
		private String filename, filetype;
		private long filesize;
		private String comment;
		
		public FileInit(JsonNode jnode) {
			super(jnode);
			this.filename = jnode.get("filename").asText();
			this.filetype = jnode.get("filetype").asText();
			this.filesize = jnode.get("filesize").asLong();
			if(jnode.has("comment"))
				comment = jnode.get("comment").asText();
		}
	}
	
	@Getter
	public class FileStart extends RecDataCommon {
		private String fileid;
		
		public FileStart(JsonNode jnode) {
			super(jnode);
			this.fileid = jnode.get("fileid").asText();
		}
	}
	
}
