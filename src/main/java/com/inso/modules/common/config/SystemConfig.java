package com.inso.modules.common.config;

public enum SystemConfig {

    /*** 风控配置 ***/

    // 是否允许提现
//    RISK_USER_WITHDRAW_ENABLE_WHEN_RECHARGE_0("risk_user_withdraw_enable_when_recharge_0", "1"),

    // 提现时触发限制打码倍数
    RISK_USER_WITHDRAW_TRIGER_LIMIT_CODEAMOUNT_MULTIPLE_NUMBER("risk_user_withdraw_triger_limit_codeamount_multiple_number", "10"),

    // 充值为0时打码
    RISK_USER_WITHDRAW_TRIGER_IMPL_WHEN_RECHARGE_0("risk_user_withdraw_triger_impl_recharge_0", "1"),

    // 当提现总额 / 充值总额 >= 5时 触发打码
    RISK_USER_WITHDRAW_TRIGER_IMPL_WITHDRAW_DIVIDE_RECHARGE_MULTIPLE_NUMBER("risk_user_withdraw_triger_impl_withdraw_divide_recharge_multiple_number", "5"),

    // 当用户余额 / 充值总额 >= 5时 触发打码
    RISK_USER_WITHDRAW_TRIGER_IMPL_BALANCE_DIVIDE_RECHARGE_MULTIPLE_NUMBER("risk_user_withdraw_triger_impl_balance_divide_recharge_multiple_number", "5"),

    /*** 自动提现配置,***/
    USER_AUTO_WITHDRAW_MAX_MONEY("user_auto_withdraw_status", "0"),


    // 下单总扣款打码
    VALID_INVITE_MEMBER_LIMIT_TOTAL_DEDUCT_CODE_AMOUNT("valid_invite_member_limit_total_deduct_code_amount", "10"),
    // 邀请会员最低充值
//    VALID_INVITE_MEMBER_LIMIT_MIN_RECHARGE_AMOUNT("valid_invite_member_limit_min_recharge_amount", "10")

    // 默认关闭
    RETURN_WATER_LAYER_LEVEL_LIMIT_MIN_PRESENT_AMOUNT("return_water_layer_level_limit_min_present_amount", "0"),
    // 按时间进行配置返佣等级, 前面7-15-30-60-90不能修改
    RETURN_WATER_LAYER_LEVEL_BY_TIME("return_water_layer_level_by_time", "7=0.8|15=0.6|30=0.5|60=0.3|90=0.1"),


    PASSPORT_CODE_AMOUNT_LIMIT_TYPE_CODE_2_BALANCE("passport_code_amount_limit_type_code_2_balance", "10"),

    // 代理补单最大上分限额
    PASSPORT_AGENT_SUPPLY_PRESENT_MAX_AMOUNT("passport_agent_supply_present_max_amount", "0"),

    GAME_PG_CODE_AMOUNT("game_pg_code_amount", "2"),
    GAME_PG_RUNNING_AMOUNT("game_pg_running_amount", "0"), // PG流水倍数

    WEB_EMAIL_REG_TPL_TITLE("web_email_reg_tpl_title", "Hi ${toEmail}, Your verification code is ${code}"),
    WEB_EMAIL_REG_TPL_DESC("web_email_reg_tpl_desc", "Hi ${toEmail}:<br><br> Your verification code is <h1>${code}</h1>"),
    ;

    private String key;
    private String subKey;
    private String value;

    public static final SystemConfig[] mArr = SystemConfig.values();


    private SystemConfig(String key, String value)
    {
        this.subKey = key;
        this.key = "system_config:" + key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getSubKey() {
        return subKey;
    }

    public String getValue() {
        return value;
    }

    public static SystemConfig getType(String key)
    {
        SystemConfig[] values = mArr;
        for(SystemConfig type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
