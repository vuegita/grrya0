package com.inso.modules.admin.config;

public interface PlatformConfig {
	
	/*** admin 后台登陆, google 认证 ***/
	public static final String ADMIN_APP_PLATFORM_GOOGLE_VALIDATE = "admin_app_platform:google_validate";

	/*** ： 是否开启员工提现审核 ***/
	public static final String ADMIN_PLATFORM_USER_WITHDRAW_CHECK_WAY_SWITCH = "admin_platform_config:user_withdraw_check_way_switch";

	/*** ： 是否开启员工提现审核 ***/
	public static final String ADMIN_PLATFORM_USER_WITHDRAW_CHECK_NETWORKTYPE_SWITCH = "admin_platform_config:user_withdraw_check_networkType_switch";

	/*** ： 是否开启员工提现审核 ***/
	public static final String ADMIN_PLATFORM_USER_WITHDRAW_CHECK_APPROVE_SWITCH = "admin_platform_config:user_withdraw_check_approve_switch";
	/*** ： 是否开启员工提现审核 ***/
	public static final String ADMIN_PLATFORM_USER_WITHDRAW_CHECK_STAFF_SWITCH = "admin_platform_config:user_withdraw_check_staff_switch";


	/*** ： 用户提现手续费用 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_FEERATE = "admin_platform_config:user_withdraw_feerate";
	/*** 平台设置：提现开始时间 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_START_TIME = "admin_platform_config:user_withdraw_start_time";
	/*** 平台设置：提现结束时间 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_END_TIME = "admin_platform_config:user_withdraw_end_time";
	/*** 平台设置-每日最大提现额 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY = "admin_platform_config:user_withdraw_max_money_of_day";
	/*** 平台设置：单笔最大提现额 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_SINGLE = "admin_platform_config:user_withdraw_max_money_of_single";
	/*** 平台设置：单笔最小体现额 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_MIN_MONEY_OF_SINGLE = "admin_platform_config:user_withdraw_min_money_of_single";
	/*** 平台设置：每日提现次数 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY = "admin_platform_config:user_withdraw_times_of_day";

	/*** 平台设置： 提现少于多少时收固定手续费 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_SOLID_MIN_AMOUNT= "admin_platform_config:user_withdraw_solid_min_amount";
	/*** 平台设置： 提现手续费 ***/
	public static final String ADMIN_APP_PLATFORM_USER_WITHDRAW_SOLID_FEEMONEY= "admin_platform_config:user_withdraw_solid_feemoney";


	/*** 平台设置：''app支付是内部跳转还是外部跳转'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_USER_RECHARGE_APP_JUMP_TYPE = "admin_platform_config:user_recharge_app_jump_type";

	/*** 平台设置：''前端下级手机号是否加密'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH = "admin_platform_config:user_phone_encryption_switch";

	/*** 平台设置：''前端活动页面是否显示'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_H5_ACTIVITY_SWITCH = "admin_platform_config:h5_activity_switch";

	/*** 平台设置：''选择H5显示模板'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_USER_SELECT_H5_DISPLAY_TEMPLATE = "admin_platform_config:user_select_h5_display_temlate";

	/*** 平台设置：'下注手续费率' ***/
	public static final String ADMIN_PLATFORM_CONFIG_GAME_BET_RATE = "admin_platform_config:game_bet_rate";

	/*** 平台设置：''签到是否开启'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_GAME_TASK_CHECKIN_SWITCH = "admin_platform_config:game_task_checkin_switch";


	/*** 平台设置：''vip0每天下载app是否开启'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_AD_VIP0_DAILY_DOWNLOAD_APP_SWITCH="admin_platform_config:ad_vip0_daily_download_app_switch";

	/*** 平台设置：''vip0提现是否开启'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_AD_VIP0_WITHDRAW_SWITCH="admin_platform_config:ad_vip0_withdraw_switch";

	/*** 平台设置：''是否开启短信注册'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_REGISTER_SWITCH = "admin_platform_config:sms_register_switch";

	/*** 平台设置：''是否开启tg用户名注册'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_TG_NAME_REGISTER_SWITCH = "admin_platform_config:tg_name_register_switch";

	/*** 平台设置：''短信内容是否带公司名'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_COMPANY_NAME_SWITCH = "admin_platform_config:sms_company_name_switch";

	/*** 平台设置：''短信参数 senderid'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_SENDERID = "admin_platform_config:sms_senderid";

	/*** 平台设置：''短信内容1'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_CONTENT_ONE = "admin_platform_config:sms_content_one";

	/*** 平台设置：''短信内容2'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_CONTENT_TWO = "admin_platform_config:sms_content_two";

	/*** 平台设置：''代理后台是否开启万能码'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_AGENT_OTP_SWITCH = "admin_platform_config:sms_agent_otp_switch";

	/*** 平台设置：''员工后台是否开启万能码'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_SMS_STAFF_OTP_SWITCH = "admin_platform_config:sms_staff_otp_switch";


	/*** 平台设置：''签到赠送金额'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_GAME_TASK_CHECKIN_AMOUNT = "admin_platform_config:game_task_checkin_amount";


	/*** 平台设置：''下注反水是否开启'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_GAME_BET_RETURN_WATER_SWITCH = "admin_platform_config:game_bet_return_water_switch";

	/*** 平台设置：''下注反水比例'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_GAME_BET_RETURN_WATER_2_SELF = "admin_platform_config:game_bet_return_water_2_self";


	/*** 下注金额按钮数值 ***/
	public static final String ADMIN_APP_PLATFORM_GAME_BET_AMOUNT_BTN_LIST = "admin_platform_config:game_bet_amount_btn_list";

	/*** USDT对卢比汇率 ***/
	public static final String ADMIN_APP_PLATFORM_USDT_TO_INR_RATE = "admin_platform_config:usdt_to_inr_rate";

	/*** 后台使用USDT对卢比汇率 ***/
	public static final String ADMIN_APP_PLATFORM_USDT_TO_INR_PLATFORM_RATE = "admin_platform_config:usdt_to_inr_platform_rate";

	/*** 后台使用USDT对马来西亚币汇率 ***/
	public static final String ADMIN_APP_PLATFORM_USDT_TO_MYR_PLATFORM_RATE = "admin_platform_config:usdt_to_myr_platform_rate";

	/*** 充值金额按钮数值 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RECHARGE_AMOUNT_BTN_LIST = "admin_platform_config:user_recharge_amount_btn_list";

	/*** 最低充值金额 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RECHARGE_MIN_AMOUNT = "admin_platform_config:user_recharge_min_amount";

	/*** 用户首充赠送比例 ***/
	public static final String ADMIN_APP_PLATFORM_USER_FIRST_RECHARGE_PRESENTATION_RATE = "admin_platform_config:user_first_recharge_presentation_rate";

	/*** 用户充值就赠送比例 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_RATE = "admin_platform_config:user_recharge_presentation_rate";

	/*** 充值输入框是否可输入小数点开关 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RECHARGE_INPUT_TYPE_SWITCH = "admin_platform_config:user_recharge_input_type_switch";

	/*** 用户充值赠送给上级比例 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_PARENTUSER_RATE = "admin_platform_config:user_recharge_presentation_parentuser_rate";

	/*** 用户购买vip金额赠送给上级比例 ***/
	public static final String ADMIN_APP_PLATFORM_USER_BUY_VIP_PRESENTATION_PARENTUSER_RATE = "admin_platform_config:user_buy_vip_presentation_parentuser_rate";

	/*** 注册赠送金额 ***/
	public static final String ADMIN_APP_PLATFORM_USER_REGISTER_PRESENTATION_AMOUNT = "admin_platform_config:user_register_presentation_amount";

	/*** 注册赠送上级金额 ***/
	public static final String ADMIN_APP_PLATFORM_USER_REGISTER_PRESENTATION_PARENTUSER_AMOUNT = "admin_platform_config:user_register_presentation_parentuser_amount";


	/*** 一级反水比例 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RETURN_WATER_1LAYER_RATE = "admin_platform_config:user_return_water_1layer_rate";
	/*** 二级反水比例 ***/
	public static final String ADMIN_APP_PLATFORM_USER_RETURN_WATER_2LAYER_RATE = "admin_platform_config:user_return_water_2layer_rate";


	/*** 邀请好友任务 ***/
	public static final String ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK = "admin_platform_config:user_invite_friend_task";


	/*** 平台设置：''首页视频链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_VIDEO_LINK = "admin_platform_config:home_video_link";


	/*** 平台设置：''twitter 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_TWITTER_LINK = "admin_platform_config:home_twitter_link";


	/*** 平台设置：''facebook 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_FACEBOOK_LINK = "admin_platform_config:home_facebook_link";


	/*** 平台设置：''telegram 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_TELEGRAM_LINK = "admin_platform_config:home_telegram_link";

	/*** 平台设置：''youtube 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_YOUTUBE_LINK = "admin_platform_config:home_youtube_link";

	/*** 平台设置：''ins 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_INS_LINK = "admin_platform_config:home_ins_link";

	/*** 平台设置：''whatsapp 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_WHATSAPP_LINK = "admin_platform_config:home_whatsapp_link";

	/*** 平台设置：''tiktok 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_HOME_TIKTOK_LINK = "admin_platform_config:home_tiktok_link";


	/*** 平台设置：''苹果下载 app链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_APPLE_LINK = "admin_platform_config:app_download_apple_link";

	/*** 平台设置：''谷歌下载app 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_GOOGLE_LINK = "admin_platform_config:app_download_google_link";

	/*** 平台设置：''安卓下载app 链接'' ***/
	public static final String ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_ANDROID_LINK = "admin_platform_config:app_download_android_link";

}
