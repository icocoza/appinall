package com.ccz.appinall.services.enums.unused;

public enum EBoardError {
	OK,
	NoSession,
	
	FailAddBoard,
	FailDeleteBoard,
	FailUpdate,
	FailAddReply,
	FailDeleteReply,
	FailAddVoteUser,
	FailDelVoteUser,
	
	NotExistLikedUser,
	NotExistDislikeUser,
	NotExistVoteUser,
	NotExistVoteInfo,

	AlreadyLiked,
	AlreadyDisliked,
	AlreadyVoteUser,
	AlreadyExpired,

	NoData,
	NoListData,
	InvalidParameter,
	PermissionDeny,
	
	WrongAptCode,
	UnknownError
}
