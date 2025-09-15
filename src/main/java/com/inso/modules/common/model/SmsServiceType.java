package com.inso.modules.common.model;

/**
 * 短信服务类型
 */
public enum SmsServiceType {
    USER_REG("user_reg", true, false, true),
    USER_LOGIN("user_login", true, false, true),
    USER_UPDATE_PWD("user_update_pwd", false, false, false),
    USER_BIND_2WF("user_bind_2wf", false, false, false),

    USER_UP_BANK_CARD("user_up_bank_card", false, true, false),
    COIN_BIND_ADDRESS("coin_bind_address", true, false, false),
    ;

    private String key;
    /*** 否允许使用系统生成的验证码, 如修改银行卡则不能 ***/
    private boolean canUsingSystemCode = false;
    /*** 强制使用注册时手机号 ***/
    private boolean requiredRegPhone = false;
    private boolean usingStaticCode = false;

    /**
     *
     * @param key
     * @param canUsingSystemCode  是否允许使用系统生成的验证码, 如修改银行卡则不能
     */
    SmsServiceType(String key, boolean canUsingSystemCode, boolean requiredRegPhone, boolean usingStaticCode)
    {
        this.key = key;
        this.canUsingSystemCode = canUsingSystemCode;
        this.requiredRegPhone = requiredRegPhone;
        this.usingStaticCode = usingStaticCode;
    }

    public String getKey() {
        return key;
    }


    public boolean isCanUsingSystemCode() {
        return canUsingSystemCode;
    }

    public boolean isRequiredRegPhone() {
        return requiredRegPhone;
    }

    public boolean isUsingStaticCode() {
        return usingStaticCode;
    }

    public static SmsServiceType getType(String key)
    {
        SmsServiceType[] values = SmsServiceType.values();
        for(SmsServiceType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
