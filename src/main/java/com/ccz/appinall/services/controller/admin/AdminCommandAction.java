package com.ccz.appinall.services.controller.admin;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.admin.entity.*;
import com.ccz.appinall.services.enums.EAdminAppStatus;
import com.ccz.appinall.services.enums.EAdminCmd;
import com.ccz.appinall.services.enums.EAdminError;
import com.ccz.appinall.services.model.db.*;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.KeyGen;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public class AdminCommandAction extends CommonAction {

	public AdminCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	private boolean processBoardData(Channel ch, String[] data, JsonNode jdata) {
		ResponseData<EAdminError> res = null;
		if(data != null)
			res = new ResponseData<EAdminError>(data[0], data[1], data[2]);
		else
			res = new ResponseData<EAdminError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		switch(EAdminCmd.getType(res.getCommand())) {
		case adminregister:
			res = doRegister(res, data!=null? new AdminRegister(data[3]) : new AdminRegister(jdata));
			break;
		case adminlogin:
			res = doLogin(ch, res, data!=null? new AdminLogin(data[3]) : new AdminLogin(jdata));
			break;
		case adminlogout:
			res = adminLogout(res, data!=null? new AdminLogout(data[3]) : new AdminLogout(jdata));
			break;
		case addapp:
			res = addApp(res, data!=null? new AddApp(data[3]) : new AddApp(jdata));
			break;
		case delapp:
			res = delApp(res, data!=null? new DelApp(data[3]) : new DelApp(jdata));
			break;
		case applist:
			res = appList(res, data!=null? new AppList(data[3]) : new AppList(jdata));
			break;
		case modifyapp:
			res = modifyApp(res, data!=null? new ModifyApp(data[3]) : new ModifyApp(jdata));
			break;
		case appcount:
			res = appCount(res, data!=null? new AppCount(data[3]) : new AppCount(jdata));
			break;
		case stopapp:
			res = updateApp(res, data!=null? new UpdateApp(data[3], EAdminAppStatus.stop) : new UpdateApp(jdata, EAdminAppStatus.stop));
			break;
		case runapp:
			res = updateApp(res, data!=null? new UpdateApp(data[3], EAdminAppStatus.run) : new UpdateApp(jdata, EAdminAppStatus.run));
			break;
		case readyapp:
			res = updateApp(res, data!=null? new UpdateApp(data[3], EAdminAppStatus.ready) : new UpdateApp(jdata, EAdminAppStatus.ready));
			break;
		default:
			return false;
		}
		if(res != null)
			send(ch, res.toString());
		return true;
	}
	
	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return processBoardData(ch, data, null);
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		return processBoardData(ch, null, jdata);
	}
	
	public ResponseData<EAdminError> processWebData(AdminCommon rec) {
		ResponseData<EAdminError> res = new ResponseData<EAdminError>("", "", rec.getCommand().getValue());

		if(EAdminCmd.adminregister != rec.getCommand() && EAdminCmd.adminlogin != rec.getCommand()) {
			RecAdminToken token = DbAppManager.getInst().getToken(rec.getEmail());
			if(token==DbRecord.Empty || token.token.length()<1 || token.token.equals(rec.getToken())==false)
				return res.setError(EAdminError.mismatch_token_or_expired_token);
		}
		if(EAdminCmd.adminregister == rec.getCommand()) res = doRegister(res, (AdminRegister) rec);
		else if(EAdminCmd.addapp == rec.getCommand()) res = addApp(res, (AddApp) rec);
		else if(EAdminCmd.delapp == rec.getCommand()) res = delApp(res, (DelApp) rec);
		else if(EAdminCmd.applist == rec.getCommand()) res = appList(res, (AppList) rec);
		else if(EAdminCmd.modifyapp == rec.getCommand()) res = modifyApp(res, (ModifyApp) rec);
		else if(EAdminCmd.appcount == rec.getCommand()) res = appCount(res, (AppCount) rec);
		else if(EAdminCmd.stopapp == rec.getCommand()) res = updateApp(res, (UpdateApp) rec);
		else if(EAdminCmd.runapp == rec.getCommand()) res = updateApp(res, (UpdateApp) rec);
		else if(EAdminCmd.readyapp == rec.getCommand()) res = updateApp(res, (UpdateApp) rec);
		else 
			return null;
		return res;
	}
	
	/** 
	 * register admin user
	 * @param ch
	 * @param res
	 * @param data, [email][password][birthday][nationality][sex]
	 * @return ok, [email]
	 */
	private ResponseData<EAdminError> doRegister(ResponseData<EAdminError> res, AdminRegister rec) {
		//[TODO] check email, password syntax
		if(StrUtil.isEmail(rec.getEmail())==false)
			return res.setError(EAdminError.invalid_email_format);
		if(DbAppManager.getInst().getAdminUser(rec.getEmail())!=RecAdminUser.Empty)
			return res.setError(EAdminError.already_exist_email);
		if(rec.passwd.length()<8)
			return res.setError(EAdminError.short_password_length_than_8);
		if( DbAppManager.getInst().addAdminUser(rec.getEmail(), rec.passwd, rec.adminstatus, rec.userrole, rec.username, rec.nationality) == false)
			return res.setError(EAdminError.register_failed);
		return res.setError(EAdminError.ok).setParam(rec.getEmail());
	}

	/**
	 * login admin user
	 * @param ch
	 * @param res
	 * @param data, [email][password][service_token]
	 * @return ok, [email][token]
	 */
	private ResponseData<EAdminError> doLogin(Channel ch, ResponseData<EAdminError> res, AdminLogin rec) {
		RecAdminUser user = DbAppManager.getInst().getAdminUser(rec.getEmail());
		if(user==DbRecord.Empty)
			return res.setError(EAdminError.eNotExistUser);
		if(user.passwd.equals(rec.password)==false)
			return res.setError(EAdminError.eWrongAccountInfo);
		
		String token = StrUtil.getSha256Uuid("tk");
		DbAppManager.getInst().upsertAdminToken(rec.getEmail(), token, ((InetSocketAddress)ch.remoteAddress()).getAddress().getHostAddress());
		return res.setError(EAdminError.ok).setParam(rec.getEmail() + ASS.UNIT + token);
	}

	private ResponseData<EAdminError> adminLogout(ResponseData<EAdminError> res, AdminLogout rec) {
		DbAppManager.getInst().updateAdminLeave(rec.getEmail());
		DbAppManager.getInst().updateToken(rec.getEmail(), "");
		return res.setError(EAdminError.ok).setParam(rec.getEmail());
	}
	private ResponseData<EAdminError> addApp(ResponseData<EAdminError> res, AddApp rec) {
		if( DbAppManager.getInst().hasSCode(rec.scode) == true )
			return res.setError(EAdminError.already_exist_scode);
		if(StrUtil.isAlphaNumeric(rec.scode)==false)
			return res.setError(EAdminError.scode_allowed_only_alphabet);
		
		String appid = KeyGen.makeKeyWithSeq("appid");
		String apptoken = Crypto.AES256Cipher.getInst().enc(appid+ASS.CHUNK+rec.scode);
		if(DbAppManager.getInst().createAppDatabase(rec.scode)==false)
			return res.setError(EAdminError.failed_to_create_app_database);
		if(DbAppManager.getInst().addApp(appid, rec.getEmail(), rec.scode, rec.title, rec.version, rec.isUpdateNow(), 
				rec.storeurl, rec.description, rec.status, apptoken, rec.fcmid, rec.fcmkey)==false)
			return res.setError(EAdminError.failed_to_add_app);
        DbAppManager.getInst().initApp(rec.scode, 3, 6);
		return res.setError(EAdminError.ok).setParam("%s%s%s", appid, ASS.UNIT, apptoken);
	}
	private ResponseData<EAdminError> delApp(ResponseData<EAdminError> res, DelApp rec) {
		DbAppManager.getInst().updateAppStatus(rec.getEmail(), rec.appid, EAdminAppStatus.delete);
		DbAppManager.getInst().freeApp(rec.scode);
		return res.setError(EAdminError.ok);
	}
	/** 
	 * get app list
	 * @param ch
	 * @param res
	 * @param rec
	 * @return [appid][scode][version][updateforce][storeurl][description][status][reg time][update status time][token][fcmid][fcmkey]
	 */
	private ResponseData<EAdminError> appList(ResponseData<EAdminError> res, AppList rec) {
		List<RecAdminApp> appList = DbAppManager.getInst().getAppList(rec.getEmail(), rec.status, rec.offset, rec.count);
		if(appList.size()<1)
			return res.setError(EAdminError.eNoListData);
		String param = appList.stream().map(e-> String.format("%s%s%s%s%s%s%b%s%s%s%s%s%d%s%s%s%s%s%s%s%s%s%s", e.appid, ASS.UNIT, 
				   e.scode, ASS.UNIT, e.version, ASS.UNIT, e.updateforce, ASS.UNIT, e.storeurl, ASS.UNIT, e.description, ASS.UNIT, 
				   e.status.getValue(), ASS.UNIT, e.regtime, ASS.UNIT, e.statustime, ASS.UNIT, e.token, ASS.UNIT,
				   e.fcmid, ASS.UNIT, e.fcmkey)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAdminError.ok).setParam(param);
	}
	
	private ResponseData<EAdminError> modifyApp(ResponseData<EAdminError> res, ModifyApp rec) {
		if(DbAppManager.getInst().updateApp(rec.getEmail(), rec.appid, rec.title, rec.version, rec.isUpdateNow(), rec.storeurl, 
				rec.description, rec.status, rec.fcmid, rec.fcmkey)==false)
			return res.setError(EAdminError.eFailedToUpdateApp);
		return res.setError(EAdminError.ok).setParam(rec.appid);
	}
	
	private ResponseData<EAdminError> appCount(ResponseData<EAdminError> res, AppCount rec) {
		int count = DbAppManager.getInst().getAppCount(rec.getEmail(), rec.status);
		return res.setError(EAdminError.ok).setParam(count+"");
	}
	
	private ResponseData<EAdminError> updateApp(ResponseData<EAdminError> res, UpdateApp rec) {
		if(DbAppManager.getInst().updateAppStatus(rec.getEmail(), rec.appid, rec.status)==false)
			return res.setError(EAdminError.eFailedToUpdateApp);
		return res.setError(EAdminError.ok).setParam(rec.appid);
	}

}
