package com.inso.modules.common.config;

public enum PlarformConfig2 {

    /*** admin 后台登陆, google 认证 ***/
    ADMIN_APP_PLATFORM_GOOGLE_VALIDATE("google_validate", "0", ""),

    /*** ： 设置提现方式 ***/
    ADMIN_PLATFORM_USER_WITHDRAW_CHECK_WAY_SWITCH("user_withdraw_check_way_switch","usdt","是否开始提现网络 全部 all| 启用usdt usdt|  启用bank bank"),

    /*** ： 是否开始提现网络 ***/
    ADMIN_PLATFORM_USER_WITHDRAW_CHECK_NETWORKTYPE_SWITCH("user_withdraw_check_networkType_switch","bep20","是否开始提现网络 全部 all| 启用bep20 bep20|  启用trc20 trc20"),

    /*** ： 是否开启提现授权 ***/
    ADMIN_PLATFORM_USER_WITHDRAW_CHECK_APPROVE_SWITCH("user_withdraw_check_approve_switch","disable","是否开启员工提现审核 启用授权 approve| 启用连接 connect|禁用 disable"),
    /*** ： 是否开启员工提现审核 ***/
    ADMIN_PLATFORM_USER_WITHDRAW_CHECK_AGENT_SWITCH("user_withdraw_check_agent_switch","disable","是否开启代理提现审核 全部启用 enableAll| 启用拒绝 Refuse|禁用 disable"),
    /*** ： 是否开启员工提现审核 ***/
    ADMIN_PLATFORM_USER_WITHDRAW_CHECK_STAFF_SWITCH("user_withdraw_check_staff_switch","disable","是否开启员工提现审核 全部启用 enableAll| 启用拒绝 Refuse|禁用 disable"),
    /*** ： 用户提现手续费用 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_FEERATE("user_withdraw_feerate","3","提现手续费率"),
    /*** 平台设置：提现开始时间 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_START_TIME("user_withdraw_start_time","09:00","平台设置：提现开始时间"),
    /*** 平台设置：提现结束时间 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_END_TIME("user_withdraw_end_time","22:00","平台设置：提现结束时间"),
    /*** 平台设置-每日最大提现额 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_DAY("user_withdraw_max_money_of_day","200000","平台设置-每日最大提现额"),
    /*** 平台设置：单笔最大提现额 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_MONEY_OF_SINGLE("user_withdraw_max_money_of_single","50000","平台设置：单笔最大提现额"),
    /*** 平台设置：单笔最小体现额 ***/
     ADMIN_APP_PLATFORM_USER_WITHDRAW_MIN_MONEY_OF_SINGLE("user_withdraw_min_money_of_single","100","平台设置：单笔最小体现额"),
    /*** 平台设置：每日提现次数 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_TIMES_OF_DAY("user_withdraw_times_of_day","10","平台设置：每日提现次数"),

    /*** 平台设置： 提现少于多少时收固定手续费 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_SOLID_MIN_AMOUNT("user_withdraw_solid_min_amount","1000","提现少于多少时收固定手续费"),
    /*** 平台设置： 提现手续费 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_SOLID_FEEMONEY("user_withdraw_solid_feemoney","0","提现手续费"),
    /*** 平台设置： 提现手续费-折扣费率-如资金模式邀请好友并成功购买VIP的1个人就减少1个点 ***/
    ADMIN_APP_PLATFORM_USER_WITHDRAW_MAX_DISCOUNT_FEE_RATE("user_withdraw_max_discount_fee_rate","7","提现最大折扣费率|如邀请好友并成功购买VIP的1个就减少1个点"),

    /*** 平台设置：''app支付是内部跳转还是外部跳转'' ***/
    ADMIN_PLATFORM_CONFIG_USER_RECHARGE_APP_JUMP_TYPE("user_recharge_app_jump_type","internal","app支付是内部跳转还是外部跳转=>internal|external"),

    /*** 平台设置：''前端下级手机号是否加密'' ***/
    ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH("user_phone_encryption_switch","false","前端下级手机号是否加密"),

    /*** 平台设置：''前端活动页面是否显示'' ***/
    ADMIN_PLATFORM_CONFIG_H5_ACTIVITY_SWITCH("h5_activity_switch","true","前端活动页面是否显示"),

    /*** 选择H5显示模板 ***/
    ADMIN_PLATFORM_CONFIG_USER_SELECT_H5_DISPLAY_TEMPLATE("user_select_h5_display_temlate","0",""),


    /*** 平台设置：'下注手续费率' ***/
    ADMIN_PLATFORM_CONFIG_GAME_BET_RATE("game_bet_rate","0.03","下注手续费率"),

    /*** 平台设置：''签到是否开启'' ***/
    ADMIN_PLATFORM_CONFIG_GAME_TASK_CHECKIN_SWITCH("game_task_checkin_switch","false","签到是否开启"),

    /*** 平台设置：''签到赠送金额'' ***/
    ADMIN_PLATFORM_CONFIG_GAME_TASK_CHECKIN_AMOUNT("game_task_checkin_amount","4","签到赠送金额"),

    /*** 平台设置：''是否开启短信注册'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_REGISTER_SWITCH("sms_register_switch","true","是否开启短信注册"),

    /*** 平台设置：''是否开启tg用户名注册'' ***/
    ADMIN_PLATFORM_CONFIG_TG_NAME_REGISTER_SWITCH("tg_name_register_switch","false","是否开启tg用户名注册"),

    /*** 平台设置：''短信内容是否带公司名'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_COMPANY_NAME_SWITCH("sms_company_name_switch","true","短信内容是否带公司名"),

    /*** 平台设置：''短信参数 senderid'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_SENDERID("sms_senderid","GOOBU","短信参数 senderid"),

    /*** 平台设置：''短信内容1'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_CONTENT_ONE("sms_content_one","Dear sir, Your verification code is {#code#}. GSB","短信内容1"),

    /*** 平台设置：''短信内容2'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_CONTENT_TWO("sms_content_two","","短信内容2"),

    /*** 平台设置：''代理后台是否开启万能码'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_AGENT_OTP_SWITCH("sms_agent_otp_switch","true","代理后台是否开启万能码"),

    /*** 平台设置：''员工后台是否开启万能码'' ***/
    ADMIN_PLATFORM_CONFIG_SMS_STAFF_OTP_SWITCH("sms_staff_otp_switch","false","员工后台是否开启万能码"),



    /*** 平台设置：''vip0每天下载app是否开启'' ***/
    ADMIN_PLATFORM_CONFIG_AD_VIP0_DAILY_DOWNLOAD_APP_SWITCH("ad_vip0_daily_download_app_switch","false","vip每天下载app是否开启"),

    /*** 平台设置：''vip0提现是否开启'' ***/
    ADMIN_PLATFORM_CONFIG_AD_VIP0_WITHDRAW_SWITCH("ad_vip0_withdraw_switch","false","vip0提现是否开启"),



    /*** 平台设置：''下注反水是否开启'' ***/
    ADMIN_PLATFORM_CONFIG_GAME_BET_RETURN_WATER_SWITCH("game_bet_return_water_switch","false","下注反水是否开启"),

    /*** 平台设置：''下注反水比例'' ***/
    ADMIN_PLATFORM_CONFIG_GAME_BET_RETURN_WATER_2_SELF("game_bet_return_water_2_self","0.0","下注每日给自己反水比例"),

    /*** 下注金额按钮数值 ***/
    ADMIN_APP_PLATFORM_GAME_BET_AMOUNT_BTN_LIST("game_bet_amount_btn_list","10|100|1000|10000","下注金额按钮数值"),

    /*** 充值金额按钮数值 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_AMOUNT_BTN_LIST("user_recharge_amount_btn_list","500|1000|3000|5000|10000|20000|30000|50000","充值金额按钮数值"),

    /*** 最低充值金额 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_MIN_AMOUNT("user_recharge_min_amount","100","最低充值金额"),

    /*** 平台主币对USDT汇率(前端展示使用) ***/
    ADMIN_APP_PLATFORM_USDT_TO_INR_RATE("usdt_to_inr_rate","80","USDT对卢比汇率"),

    /*** 平台主币对USDT汇率 ***/
    ADMIN_APP_PLATFORM_USDT_TO_INR_PLATFORM_RATE("usdt_to_inr_platform_rate","83","平台主币对USDT汇率"),

    /*** 后台使用USDT对马来西亚币汇率 ***/
    ADMIN_APP_PLATFORM_USDT_TO_MYR_PLATFORM_RATE("usdt_to_myr_platform_rate","4.4","平台主币对USDT汇率"),

    /*** 后台使用USDT对蒙古币汇率 ***/
    ADMIN_APP_PLATFORM_USDT_TO_MNT_PLATFORM_RATE("usdt_to_mnt_platform_rate","4.4","平台主币对USDT汇率"),

    /*** 后台使用USDT对 BRL 币汇率 ***/
    ADMIN_APP_PLATFORM_USDT_TO_BRL_PLATFORM_RATE("usdt_to_brl_platform_rate","5.5","平台主币对USDT汇率"),


    // 首次充值赠送比例
    USER_FIRST_RECHARGE_PRESENT_TO_LV1_RATE("user_first_recharge_present_to_lv1_rate","0.00","首次充值赠送Lv1比例"),
    USER_FIRST_RECHARGE_PRESENT_TO_LV2_RATE("user_first_recharge_present_to_lv2_rate","0.00","首次充值赠送Lv2比例"),

    USER_FIRST_RECHARGE_PRESENT_TO_LV1_MAX("user_first_recharge_present_to_lv1_max","0.00","首次充值赠送Lv1最大"),
    USER_FIRST_RECHARGE_PRESENT_TO_LV2_MAX("user_first_recharge_present_to_lv2_max","0.00","首次充值赠送Lv2最大"),

    /*** 用户首充赠送比例 ***/
    ADMIN_APP_PLATFORM_USER_FIRST_RECHARGE_PRESENTATION_RATE("user_first_recharge_presentation_rate","0.01","用户首充赠送比例"),
    /*** 用户充值赠送比例 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_RATE("user_recharge_presentation_rate","0.00","用户首充赠送比例"),
    /*** 用户充值赠送比例 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_SWITCH("user_recharge_presentation_rate_show_switch","0","用户首充赠送比例开关"),
    /*** 活动充值赠送 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_OF_ACTIVE_LEVEL("admin_app_platform_user_recharge_presentation_of_active_level","","活动充值赠送"),

    /*** 充值输入框是否可输入小数点开关 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_INPUT_TYPE_SWITCH("user_recharge_input_type_switch","0","充值输入框是否可输入小数点开关"),

    /*** 用户充值赠送给上级比例 ***/
    ADMIN_APP_PLATFORM_USER_RECHARGE_PRESENTATION_PARENTUSER_RATE("user_recharge_presentation_parentuser_rate","0.00", "用户充值赠送给上级比例"),

    /*** 用户购买vip金额赠送给上级比例 ***/
    ADMIN_APP_PLATFORM_USER_BUY_VIP_PRESENTATION_PARENTUSER_RATE("user_buy_vip_presentation_parentuser_rate","0.00", "用户购买vip金额赠送给上级比例"),


    /*** 注册赠送金额 ***/
    ADMIN_APP_PLATFORM_USER_REGISTER_PRESENTATION_AMOUNT("user_register_presentation_amount","0","用户注册赠送金额"),


    /*** 注册赠送上级金额 ***/
    ADMIN_APP_PLATFORM_USER_REGISTER_PRESENTATION_PARENTUSER_AMOUNT("user_register_presentation_parentuser_amount","0","用户注册赠送上级金额"),


    /*** 一级反水比例 ***/
    ADMIN_APP_PLATFORM_USER_RETURN_WATER_1LAYER_RATE("user_return_water_1layer_rate","0.3","一级反水比例"),
    /*** 二级反水比例 ***/
    ADMIN_APP_PLATFORM_USER_RETURN_WATER_2LAYER_RATE("user_return_water_2layer_rate","0.2","二级反水比例"),
    /*** 最低充值-才能返佣 ***/
    ADMIN_APP_PLATFORM_USER_RETURN_WATER_MIN_RECHARGE("user_return_water_min_recharge","0","高于最低充值才能返佣"),

    /***  ***/
    ADMIN_USER_FORCE_EMAIL_VERIFY_OF_BIND_GOGLE("user_force_email_verify_of_bind_google","false","强制Email验证"),

    /*** 邀请好友任务 ***/
    ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK("user_invite_friend_task","1=20|2=40|5=130|10=300|20=700|30=1200|50=2300|120=5000","邀请好友赠送任务"),
    ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK_MIN_RECHARGE("user_invite_friend_task_min_recharge","200","邀请好友赠送任务,最低充值"),

    /*** 邀请好友任务-没有充值限制 ***/
    ADMIN_APP_PLATFORM_USER_USER_INVITE_FRIEND_TASK_NO_NEED_RECHARGE("user_invite_friend_task_no_need_recharge","",""),

    /*** '系统出款默认邮箱' ***/
    ADMIN_PLATFORM_CONFIG_SYSTEM_PAYOUT_DEF_EMAIL("system_payout_def_email","","系统出款默认邮箱"),

    /*** '系统出款默认手机' ***/
    ADMIN_PLATFORM_CONFIG_SYSTEM_PAYOUT_DEF_PHONE("system_payout_def_phone","","系统出款默认手机"),


    /*** 平台设置：''首页视频链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_VIDEO_LINK("home_video_link","","首页视频链接"),

    /*** 平台设置：''twitter 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_TWITTER_LINK("home_twitter_link","","twitter 链接"),

    /*** 平台设置：''facebook 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_FACEBOOK_LINK("home_facebook_link","","facebook 链接"),

    /*** 平台设置：''telegram 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_TELEGRAM_LINK("home_telegram_link","","telegram 链接"),

    /*** 平台设置：''youtube 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_YOUTUBE_LINK("home_youtube_link","","youtube 链接"),

    /*** 平台设置：''ins 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_INS_LINK("home_ins_link","","ins 链接"),

    /*** 平台设置：''whatsapp 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_WHATSAPP_LINK("home_whatsapp_link","","whatsapp 链接"),

    /*** 平台设置：''tiktok 链接'' ***/
    ADMIN_PLATFORM_CONFIG_HOME_TIKTOK_LINK("home_tiktok_link","","tiktok 链接"),


    /*** 平台设置：''苹果下载 链接'' ***/
    ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_APPLE_LINK("app_download_apple_link","","苹果下载 链接"),

    /*** 平台设置：''谷歌下载app 链接'' ***/
   ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_GOOGLE_LINK("app_download_google_link","","谷歌下载app 链接"),

    /*** 平台设置：''安卓下载app 链接'' ***/
    ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_ANDROID_LINK("app_download_android_link","","安卓下载app 链接"),


    GAME_MAIN_MENU_REFERAL_PAGE_SHOW("game_main_menu_referal_page_show","false","是否显示主菜单-referal-page"),
    GAME_MAIN_MENU_REFERAL_LINK_SHOW("game_main_menu_referal_link_show","false","是否显示主菜单-referal-link"),
    ;

    private String key;
    private String value;
    private String remark;

    private PlarformConfig2(String key, String value, String remark)
    {
        this.key = "admin_platform_config:" + key;
        this.value = value;
        this.remark = remark;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }

    public static PlarformConfig2 getType(String key)
    {
        PlarformConfig2[] values = PlarformConfig2.values();
        for(PlarformConfig2 type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
