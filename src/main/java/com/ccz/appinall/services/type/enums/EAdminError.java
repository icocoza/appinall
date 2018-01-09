package com.ccz.appinall.services.type.enums;

public enum EAdminError {
	ok,
	
	/*for the register*/
	already_exist_email,
	short_password_length_than_8,
	register_failed,

	/*for the add app*/
	already_exist_scode,
	scode_allowed_only_alphabet,
	failed_to_create_app_database,
	failed_to_add_app,

	eWrongAccountInfo,
	
	eFailedToUpdateApp,
	
	eNoListData,
	eNotExistUser,
	
	/*for common*/
	mismatch_token_or_expired_token,
	
}
