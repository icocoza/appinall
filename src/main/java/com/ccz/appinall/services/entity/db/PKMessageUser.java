package com.ccz.appinall.services.entity.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PKMessageUser implements Serializable {

	private static final long serialVersionUID = 7276540679234462830L;

	@Column(length = 64, nullable = false)
	private String userid;

	@Column(length = 64, nullable = false)
	private String msgid;
	
	public PKMessageUser(String userid, String msgid) {
		this.userid = userid;
		this.msgid = msgid;
	}
}
