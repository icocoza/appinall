package com.ccz.appinall.services.controller.admin;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.admin.entity.*;
import com.ccz.appinall.services.enums.EAdminAppStatus;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.model.db.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.KeyGen;

import io.netty.channel.Channel;

@Configuration
public class AdminCommandAction extends CommonAction {
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public AdminCommandAction() {
		super.setCommandFunction(EAllCmd.adminregister, doRegister);
		super.setCommandFunction(EAllCmd.adminlogin, doLogin);
		super.setCommandFunction(EAllCmd.adminlogout, adminLogout);
		super.setCommandFunction(EAllCmd.addapp, addApp);
		super.setCommandFunction(EAllCmd.delapp, delApp);
		super.setCommandFunction(EAllCmd.applist, appList);
		super.setCommandFunction(EAllCmd.modifyapp, modifyApp);
		super.setCommandFunction(EAllCmd.appcount, appCount);
		super.setCommandFunction(EAllCmd.stopapp, updateApp);
		super.setCommandFunction(EAllCmd.runapp, updateApp);
		super.setCommandFunction(EAllCmd.readyapp, updateApp);
	}
	

	public ResponseData<EAllError> processWebData(AdminCommon rec) {
		ResponseData<EAllError> res = new ResponseData<EAllError>("", "", rec.getCommand().getValue());
		
		if(EAllCmd.adminregister != rec.getCommand() && EAllCmd.adminlogin != rec.getCommand()) {
			RecAdminToken token = DbAppManager.getInst().getToken(rec.getEmail());
			if(token==DbRecord.Empty || token.token.length()<1 || token.token.equals(rec.getToken())==false)
				return res.setError(EAllError.mismatch_token_or_expired_token);
		}
		
		ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> cmdFunc = super.getCommandFunction(rec.getCommand().getValue());
		if(cmdFunc == null) 
			return res.setError(EAllError.unknown_error);
		return cmdFunc.doAction((Channel)null, res, rec);
	}
	
	/** 
	 * register admin user
	 * @param ch
	 * @param res
	 * @param data, [email][password][birthday][nationality][sex]
	 * @return ok, [email]
	 */
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> doRegister = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		AdminRegister data = (AdminRegister)rec;
		//[TODO] check email, password syntax
		if(StrUtil.isEmail(data.getEmail())==false)
			return res.setError(EAllError.invalid_email_format);
		if(DbAppManager.getInst().getAdminUser(data.getEmail())!=RecAdminUser.Empty)
			return res.setError(EAllError.already_exist_email);
		if(data.passwd.length()<8)
			return res.setError(EAllError.short_password_length_than_8);
		if( DbAppManager.getInst().addAdminUser(data.getEmail(), data.passwd, data.adminstatus, data.userrole, data.username, data.nationality) == false)
			return res.setError(EAllError.register_failed);
		return res.setError(EAllError.ok).setParam(data.getEmail());
	};

	/**
	 * login admin user
	 * @param ch
	 * @param res
	 * @param data, [email][password][service_token]
	 * @return ok, [email][token]
	 */
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> doLogin = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		AdminLogin data = (AdminLogin)rec;
		RecAdminUser user = DbAppManager.getInst().getAdminUser(data.getEmail());
		if(user==DbRecord.Empty)
			return res.setError(EAllError.eNotExistUser);
		if(user.passwd.equals(data.password)==false)
			return res.setError(EAllError.eWrongAccountInfo);
		
		String token = StrUtil.getSha256Uuid("tk");
		DbAppManager.getInst().upsertAdminToken(data.getEmail(), token, ((InetSocketAddress)ch.remoteAddress()).getAddress().getHostAddress());
		return res.setError(EAllError.ok).setParam(data.getEmail() + ASS.UNIT + token);
	};

	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> adminLogout = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		AdminLogout data = (AdminLogout)rec;
		DbAppManager.getInst().updateAdminLeave(data.getEmail());
		DbAppManager.getInst().updateToken(data.getEmail(), "");
		return res.setError(EAllError.ok).setParam(data.getEmail());
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> addApp = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		AddApp data = (AddApp)rec;
		if( DbAppManager.getInst().hasSCode(data.scode) == true )
			return res.setError(EAllError.already_exist_scode);
		if(StrUtil.isAlphaNumeric(data.scode)==false)
			return res.setError(EAllError.scode_allowed_only_alphabet);
		
		String appid = KeyGen.makeKeyWithSeq("appid");
		String apptoken = Crypto.AES256Cipher.getInst().enc(appid+ASS.CHUNK+data.scode);
		if(DbAppManager.getInst().createAppDatabase(data.scode)==false)
			return res.setError(EAllError.failed_to_create_app_database);
		if(DbAppManager.getInst().addApp(appid, data.getEmail(), data.scode, data.title, data.version, data.isUpdateNow(), 
				data.storeurl, data.description, data.status, apptoken, data.fcmid, data.fcmkey)==false)
			return res.setError(EAllError.failed_to_add_app);
        DbAppManager.getInst().initApp(data.scode, 3, 6);
		return res.setError(EAllError.ok).setParamFormat("%s%s%s", appid, ASS.UNIT, apptoken);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> delApp = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		DelApp data = (DelApp)rec;
		DbAppManager.getInst().updateAppStatus(data.getEmail(), data.appid, EAdminAppStatus.delete);
		DbAppManager.getInst().freeApp(data.scode);
		return res.setError(EAllError.ok);
	};
	/** 
	 * get app list
	 * @param ch
	 * @param res
	 * @param data
	 * @return [appid][scode][version][updateforce][storeurl][description][status][reg time][update status time][token][fcmid][fcmkey]
	 */
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> appList = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		AppList data = (AppList)rec;
		List<RecAdminApp> appList = DbAppManager.getInst().getAppList(data.getEmail(), data.status, data.offset, data.count);
		if(appList.size()<1)
			return res.setError(EAllError.eNoListData);
		String param = appList.stream().map(e-> String.format("%s%s%s%s%s%s%b%s%s%s%s%s%d%s%s%s%s%s%s%s%s%s%s", e.appid, ASS.UNIT, 
				   e.scode, ASS.UNIT, e.version, ASS.UNIT, e.updateforce, ASS.UNIT, e.storeurl, ASS.UNIT, e.description, ASS.UNIT, 
				   e.status, ASS.UNIT, e.regtime, ASS.UNIT, e.statustime, ASS.UNIT, e.token, ASS.UNIT,
				   e.fcmid, ASS.UNIT, e.fcmkey)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> modifyApp = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		ModifyApp data = (ModifyApp)rec;
		if(DbAppManager.getInst().updateApp(data.getEmail(), data.appid, data.title, data.version, data.isUpdateNow(), data.storeurl, 
				data.description, data.status, data.fcmid, data.fcmkey)==false)
			return res.setError(EAllError.eFailedToUpdateApp);
		return res.setError(EAllError.ok).setParam(data.appid);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> appCount = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		AppCount data = (AppCount)rec;
		int count = DbAppManager.getInst().getAppCount(data.getEmail(), data.status);
		return res.setError(EAllError.ok).setParam(count+"");
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, AdminCommon> updateApp = (Channel ch, ResponseData<EAllError> res, AdminCommon rec) -> {
		UpdateApp data = (UpdateApp)rec;
		if(DbAppManager.getInst().updateAppStatus(data.getEmail(), data.appid, data.status)==false)
			return res.setError(EAllError.eFailedToUpdateApp);
		return res.setError(EAllError.ok).setParam(data.appid);
	};

}
