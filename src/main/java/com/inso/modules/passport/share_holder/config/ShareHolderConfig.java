package com.inso.modules.passport.share_holder.config;

/**
 *
 */
public enum ShareHolderConfig {

    LV1_LIMIT_MIN_INVITE_COUNT("lv1_limit_min_invite_count", "100"), // 最低邀请
    LV1_LIMIT_MIN_RECHARGE_AMOUNT("lv1_limit_min_recharge_amount", "10000"), // 最低邀请

    LV2_LIMIT_MIN_INVITE_COUNT("lv2_limit_min_invite_count", "200"), // 最低邀请
    LV2_LIMIT_MIN_RECHARGE_AMOUNT("lv2_limit_min_recharge_amount", "20000"), // 最低邀请

    CONTACT_US("contact_us", ""), //客服链接链接
    CONTACT_US_H5_SHOW_switch("contact_us_h5_show_switch", "true"), //前端是否显示加入我们

    REMAINING_COUNT("remaining_count", "200"),

    CONTACT_US_GROUP("contact_us_group", ""), //加入我们群链接

    ;

    private String key;
    private String subkey;
    private String value;


    private ShareHolderConfig(String subkey, String value)
    {
        this.subkey = subkey;
        this.key = "passport_share_holder:" + subkey;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getSubkey() {
        return subkey;
    }

    public String getValue() {
        return value;
    }



}
