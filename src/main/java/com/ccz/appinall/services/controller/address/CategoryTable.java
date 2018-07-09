package com.ccz.appinall.services.controller.address;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryTable {
	@JsonIgnore
	private String tableid;
	private String title;
	private int category;

	public CategoryTable(String tableid, String title, int category) {
		this.tableid = tableid;
		this.title = title;
		this.category = category;
	}
}

