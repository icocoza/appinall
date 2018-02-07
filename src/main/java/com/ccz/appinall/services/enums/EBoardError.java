package com.ccz.appinall.services.enums;

public enum EBoardError {
	eOK,
	eNoSession,
	
	eFailAddBoard,
	eFailDeleteBoard,
	eFailUpdate,
	eFailAddReply,
	eFailDeleteReply,
	eFailAddVoteUser,
	eFailDelVoteUser,
	
	eNotExistLikedUser,
	eNotExistDislikeUser,
	eNotExistVoteUser,
	eNotExistVoteInfo,

	eAlreadyLiked,
	eAlreadyDisliked,
	eAlreadyVoteUser,
	eAlreadyExpired,

	eNoData,
	eNoListData,
	eInvalidParameter,
	ePermissionDeni,
	
	eWrongAptCode
}
