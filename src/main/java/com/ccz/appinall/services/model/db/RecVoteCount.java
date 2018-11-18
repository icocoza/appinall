package com.ccz.appinall.services.model.db;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecVoteCount extends DbRecord {
	static final String TBL_NAME = "voteuser";
	public String boardid;
	public int votecount=0;
	
	public RecVoteCount(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		return false;
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecVoteCount rec = (RecVoteCount)r;
		
		rec.boardid = rd.getString("boardid");
		rec.votecount = rd.getInt("votecount");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecVoteCount(poolName));
	}
	
	public Map<String, Integer>  getVoteCount(List<String> boardids) {
		String qBoardids = boardids.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
		String sql = String.format("SELECT boardid, count(boardid) as votecount FROM %s WHERE boardid in (%s) group by boardid", RecVoteCount.TBL_NAME, qBoardids);
		return super.getList(sql).stream().map(e->(RecVoteCount)e).collect(Collectors.toMap(x->x.boardid, x->x.votecount));
	}

	public Map<String, Integer>  getVotedBoardId(String userid, List<String> boardids) {
		String qBoardids = boardids.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
		String sql = String.format("SELECT boardid FROM %s WHERE boardid in (%s) AND userid='%s'", RecVoteCount.TBL_NAME, qBoardids, userid);
		return super.getList(sql).stream().map(e->(RecVoteCount)e).collect(Collectors.toMap(x->x.boardid, x->x.votecount));
	}

}
