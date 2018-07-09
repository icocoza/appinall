package com.ccz.appinall.services.controller.address;

import lombok.Getter;

@Getter
public class BuildingInfo {
	private String buildId;
	private String buildName;
	
	public BuildingInfo(String buildId, String buildName) {
		this.buildId = buildId;
		this.buildName = buildName;
	}
}
