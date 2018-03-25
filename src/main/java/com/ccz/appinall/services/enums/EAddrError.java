package com.ccz.appinall.services.enums;

public enum EAddrError {
	ok,
	
	failed_search,
	invalid_search,
	empty_search,
	no_search_result,
	
	invalid_from_addressid,
	invalid_to_addressid,
	
	empty_goods_name,
	empty_goods_size,
	empty_goods_weight,
	empty_goods_type,
	empty_goods_price,
	empty_order_begintime,
	empty_order_endtime,
	empty_gpslist,
	failed_to_saveorder,
	
	invalid_offset_count,
	empty_order_list,
	no_order_data,
	
	not_exist_order,
	late_delivery_request,
	already_assigned_order,
	failed_assign_deliver,
	not_authorized_user,
	not_assigned_order,
	not_allowed_order,
	not_started_order,
	not_receipt_order,
	not_delivering_order,
	not_delivered_order,
	already_starting_order,
	failed_cancel_delivery_ready,
	already_occupied_order,
	failed_apply_order,
	failed_to_saveassign,
	failed_to_savestartmoving,
	failed_to_savegotcha,
	failed_to_savedelivering,
	failed_to_savedelivered,
	failed_to_saveconfirm,
	impossible_cancel_delivery,
	invalid_start_passcode,
	invalid_end_passcode,
	no_permission,
	not_exist_deliver,
	
	unknown_error
}
