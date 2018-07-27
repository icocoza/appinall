package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecBoardDetail extends RecBoard {
	
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
		String sql = String.format("SELECT * FROM board JOIN boardcount ON(board.boardid = boardcount.boardid) WHERE board.category='%s' ORDER BY board.createtime DESC LIMIT %d, %d", category, offset, count);
		return super.getList(sql).stream().map(e->(RecBoardDetail)e).collect(Collectors.toList());
	}
}
