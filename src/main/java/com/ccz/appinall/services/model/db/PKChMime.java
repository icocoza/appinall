package com.ccz.appinall.services.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PKChMime implements Serializable {

	private static final long serialVersionUID = -9016253008632332410L;

	@Column(length = 64, nullable = false)
	private String userid;

	@Column(length = 64, nullable = false)
	private String chid;
	
	public PKChMime(String userid, String chid) {
		this.userid = userid;
		this.chid = chid;
	}
	
}
