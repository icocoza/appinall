package com.ccz.appinall.services.enums.unused;

public enum EAuthError {
	ok,
	wrong_appid,
	unknown_datatype,
	unknown_error,
	
	failed_register,
	already_exist_userid,
	already_exist_email,
	already_exist_phoneno,
	
	userid_more_than_8,
	userid_alphabet_and_digit,
	pass_more_than_6,
	
	invalid_email_format,
	invalid_phoneno_format,
	smscode_size_4,
	mismatch_smscode,
	
	invalid_app_token,
	invalid_user_token,
	invalid_user_tokenid,
	invalid_user_uuid,
	unauthorized_token,
	
	mismatch_token,
	not_exist_user,
	not_exist_userinfo,
	not_exist_building,
	
	invalid_or_expired_token,
	unauthorized_userid,
	invalid_user,
	invalid_uuid,
	failed_email_verify,
	failed_phone_Verify,
	failed_change_pw,
	failed_update_token,
	mismatch_pw,
	
	eNotExistIds,
	
	
	eNoSession
}
