package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecUserVoterView extends RecUserVoter {
	public String username;
	
	public RecUserVoterView(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		return false;
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		super.doLoad(rd, r);
		username = rd.getString("username");
		return null;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUserVoterView(super.poolName));
	}
	
	public List<RecUserVoterView> getVoterUserList(String deliverid, int offset, int count) {
		String sql = String.format("SELECT uservoter.deliverid, uservoter.orderid, uservoter.senderid, uservoter.point, "
				+ "uservoter.like, uservoter.comments, uservoter.votetime, user.username "
				+ "FROM uservoter join user on user.userid = uservoter.senderid "
				+ "WHERE deliverid='%s' LIMIT %d, %d", deliverid, offset, count);
		return super.getList(sql).stream().map(e->(RecUserVoterView)e).collect(Collectors.toList());
	}

}
