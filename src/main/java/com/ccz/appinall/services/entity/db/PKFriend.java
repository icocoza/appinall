package com.ccz.appinall.services.entity.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PKFriend  implements Serializable {

	private static final long serialVersionUID = 9093735121666652545L;

	@Column(length = 64, nullable = false)
	private String userid;

	@Column(length = 64, nullable = false)
	private String friendid;
	
	public PKFriend(String userid, String friendid) {
		this.userid = userid;
		this.friendid = friendid;
	}
	
}