package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecBoardDetail extends RecBoard {
	
	@JsonIgnore private String serverip, subpath, filename;
	public String cropurl;
	public int likes = 0, dislikes=0, visit=0, reply=0;
	
	public RecBoardDetail(String poolName) {
		super(poolName);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardDetail rec = (RecBoardDetail)super.doLoad(rd, r);
		rec.likes = rd.getInt("likes");
		rec.dislikes = rd.getInt("dislikes");
		rec.visit = rd.getInt("visit");
		rec.reply = rd.getInt("reply");
		
		rec.serverip = rd.getString("serverip");
		rec.subpath = rd.getString("subpath");
		rec.filename = rd.getString("filename");
		rec.cropurl = getCropfilePath(rec);
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardDetail(poolName));
	}
	
	public List<RecBoardDetail> getBoardList(String category, int offset,  int count) {
		String sql = String.format("SELECT * FROM board " + 
				"LEFT JOIN boardcount ON board.boardid=boardcount.boardid " + 
				"LEFT JOIN filecrop ON board.boardid=filecrop.boardid " + 
				"WHERE board.category='%s' AND board.deleted=false ORDER BY board.createtime DESC LIMIT %d, %d", category, offset, count);
		return super.getList(sql).stream().map(e->(RecBoardDetail)e).collect(Collectors.toList());
	}
	
	public String getCropfilePath(RecBoardDetail rec) {
		if(rec.filename==null || rec.filename.length()<1)
			return null;
		return String.format("http://%s:8080/%s?boardid=%s&scode=%s", rec.serverip, rec.subpath, rec.boardid, poolName);
	}
}
