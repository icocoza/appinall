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
import com.ccz.appinall.services.enums.EAdminCmd;
import com.ccz.appinall.services.enums.EAdminError;
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
		super.setCommandFunction(EAdminCmd.adminregister.getValue(), doRegister);
		super.setCommandFunction(EAdminCmd.adminlogin.getValue(), doLogin);
		super.setCommandFunction(EAdminCmd.adminlogout.getValue(), adminLogout);
		super.setCommandFunction(EAdminCmd.addapp.getValue(), addApp);
		super.setCommandFunction(EAdminCmd.delapp.getValue(), delApp);
		super.setCommandFunction(EAdminCmd.applist.getValue(), appList);
		super.setCommandFunction(EAdminCmd.modifyapp.getValue(), modifyApp);
		super.setCommandFunction(EAdminCmd.appcount.getValue(), appCount);
		super.setCommandFunction(EAdminCmd.stopapp.getValue(), updateApp);
		super.setCommandFunction(EAdminCmd.runapp.getValue(), updateApp);
		super.setCommandFunction(EAdminCmd.readyapp.getValue(), updateApp);
	}
	

	@Override
	public boolean processCommand(Channel ch, JsonNode jdata) {
		// TODO Auto-generated method stub
		return false;
	}

	public ResponseData<EAdminError> processWebData(AdminCommon rec) {
		ResponseData<EAdminError> res = new ResponseData<EAdminError>("", "", rec.getCommand().getValue());
		
		if(EAdminCmd.adminregister != rec.getCommand() && EAdminCmd.adminlogin != rec.getCommand()) {
			RecAdminToken token = DbAppManager.getInst().getToken(rec.getEmail());
			if(token==DbRecord.Empty || token.token.length()<1 || token.token.equals(rec.getToken())==false)
				return res.setError(EAdminError.mismatch_token_or_expired_token);
		}
		
		ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> cmdFunc = super.getCommandFunction(rec.getCommand().getValue());
		if(cmdFunc == null) 
			return res.setError(EAdminError.unknown_command);
		return cmdFunc.doAction((Channel)null, res, rec);
	}
	
	/** 
	 * register admin user
	 * @param ch
	 * @param res
	 * @param data, [email][password][birthday][nationality][sex]
	 * @return ok, [email]
	 */
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> doRegister = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		AdminRegister data = (AdminRegister)rec;
		//[TODO] check email, password syntax
		if(StrUtil.isEmail(data.getEmail())==false)
			return res.setError(EAdminError.invalid_email_format);
		if(DbAppManager.getInst().getAdminUser(data.getEmail())!=RecAdminUser.Empty)
			return res.setError(EAdminError.already_exist_email);
		if(data.passwd.length()<8)
			return res.setError(EAdminError.short_password_length_than_8);
		if( DbAppManager.getInst().addAdminUser(data.getEmail(), data.passwd, data.adminstatus, data.userrole, data.username, data.nationality) == false)
			return res.setError(EAdminError.register_failed);
		return res.setError(EAdminError.ok).setParam(data.getEmail());
	};

	/**
	 * login admin user
	 * @param ch
	 * @param res
	 * @param data, [email][password][service_token]
	 * @return ok, [email][token]
	 */
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> doLogin = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		AdminLogin data = (AdminLogin)rec;
		RecAdminUser user = DbAppManager.getInst().getAdminUser(data.getEmail());
		if(user==DbRecord.Empty)
			return res.setError(EAdminError.eNotExistUser);
		if(user.passwd.equals(data.password)==false)
			return res.setError(EAdminError.eWrongAccountInfo);
		
		String token = StrUtil.getSha256Uuid("tk");
		DbAppManager.getInst().upsertAdminToken(data.getEmail(), token, ((InetSocketAddress)ch.remoteAddress()).getAddress().getHostAddress());
		return res.setError(EAdminError.ok).setParam(data.getEmail() + ASS.UNIT + token);
	};

	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> adminLogout = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		AdminLogout data = (AdminLogout)rec;
		DbAppManager.getInst().updateAdminLeave(data.getEmail());
		DbAppManager.getInst().updateToken(data.getEmail(), "");
		return res.setError(EAdminError.ok).setParam(data.getEmail());
	};
	
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> addApp = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		AddApp data = (AddApp)rec;
		if( DbAppManager.getInst().hasSCode(data.scode) == true )
			return res.setError(EAdminError.already_exist_scode);
		if(StrUtil.isAlphaNumeric(data.scode)==false)
			return res.setError(EAdminError.scode_allowed_only_alphabet);
		
		String appid = KeyGen.makeKeyWithSeq("appid");
		String apptoken = Crypto.AES256Cipher.getInst().enc(appid+ASS.CHUNK+data.scode);
		if(DbAppManager.getInst().createAppDatabase(data.scode)==false)
			return res.setError(EAdminError.failed_to_create_app_database);
		if(DbAppManager.getInst().addApp(appid, data.getEmail(), data.scode, data.title, data.version, data.isUpdateNow(), 
				data.storeurl, data.description, data.status, apptoken, data.fcmid, data.fcmkey)==false)
			return res.setError(EAdminError.failed_to_add_app);
        DbAppManager.getInst().initApp(data.scode, 3, 6);
		return res.setError(EAdminError.ok).setParamFormat("%s%s%s", appid, ASS.UNIT, apptoken);
	};
	
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> delApp = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		DelApp data = (DelApp)rec;
		DbAppManager.getInst().updateAppStatus(data.getEmail(), data.appid, EAdminAppStatus.delete);
		DbAppManager.getInst().freeApp(data.scode);
		return res.setError(EAdminError.ok);
	};
	/** 
	 * get app list
	 * @param ch
	 * @param res
	 * @param data
	 * @return [appid][scode][version][updateforce][storeurl][description][status][reg time][update status time][token][fcmid][fcmkey]
	 */
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> appList = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		AppList data = (AppList)rec;
		List<RecAdminApp> appList = DbAppManager.getInst().getAppList(data.getEmail(), data.status, data.offset, data.count);
		if(appList.size()<1)
			return res.setError(EAdminError.eNoListData);
		String param = appList.stream().map(e-> String.format("%s%s%s%s%s%s%b%s%s%s%s%s%d%s%s%s%s%s%s%s%s%s%s", e.appid, ASS.UNIT, 
				   e.scode, ASS.UNIT, e.version, ASS.UNIT, e.updateforce, ASS.UNIT, e.storeurl, ASS.UNIT, e.description, ASS.UNIT, 
				   e.status.getValue(), ASS.UNIT, e.regtime, ASS.UNIT, e.statustime, ASS.UNIT, e.token, ASS.UNIT,
				   e.fcmid, ASS.UNIT, e.fcmkey)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAdminError.ok).setParam(param);
	};
	
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> modifyApp = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		ModifyApp data = (ModifyApp)rec;
		if(DbAppManager.getInst().updateApp(data.getEmail(), data.appid, data.title, data.version, data.isUpdateNow(), data.storeurl, 
				data.description, data.status, data.fcmid, data.fcmkey)==false)
			return res.setError(EAdminError.eFailedToUpdateApp);
		return res.setError(EAdminError.ok).setParam(data.appid);
	};
	
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> appCount = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		AppCount data = (AppCount)rec;
		int count = DbAppManager.getInst().getAppCount(data.getEmail(), data.status);
		return res.setError(EAdminError.ok).setParam(count+"");
	};
	
	ICommandFunction<Channel, ResponseData<EAdminError>, AdminCommon> updateApp = (Channel ch, ResponseData<EAdminError> res, AdminCommon rec) -> {
		UpdateApp data = (UpdateApp)rec;
		if(DbAppManager.getInst().updateAppStatus(data.getEmail(), data.appid, data.status)==false)
			return res.setError(EAdminError.eFailedToUpdateApp);
		return res.setError(EAdminError.ok).setParam(data.appid);
	};

}
