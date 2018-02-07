package com.ccz.appinall.services.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PKVoteUser  implements Serializable {
	private static final long serialVersionUID = -6857811634096207530L;

	@Column(length = 64, nullable = false)
	private String boardid;
	
	@Column(length = 64, nullable = false)
	private String userid;
	
	public PKVoteUser(String boardid, String userid) {
		this.boardid = boardid;
		this.userid = userid;
	}
}