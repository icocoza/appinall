package com.ccz.appinall.services.entity.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PKBoardUser implements Serializable {
	private static final long serialVersionUID = 7398583347655308187L;

	@Column(length = 64, nullable = false)
	private String boardid;
	
	@Column(length = 64, nullable = false)
	private String userid;
	
	@Column(length = 16, nullable = false)
	private String preference;
	
	public PKBoardUser(String boardid, String userid, String preference) {
		this.boardid = boardid;
		this.userid = userid;
		this.preference = preference;
	}
}
