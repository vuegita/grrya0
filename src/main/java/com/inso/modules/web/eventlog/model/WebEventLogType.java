package com.inso.modules.web.eventlog.model;

import org.springframework.ui.Model;

public enum WebEventLogType {

    MEMBER_LOGIN("member_login", "会员登陆", false),
    MEMBER_GOOGLE_BIND("member_google_bind", "2WF绑定", false),
    MEMBER_UPDATE_WITHDRAW_ADDR("member_update_withdraw_addr", "修改提现地址", false),


    LOGIN("Login", "登陆事件", true),

    PASSPORT_READ_GOOGLE_KEY("passport_read_google_key", "读取用户谷歌", true),

    ADMIN_EDIT("admin_edit", "编辑admin", true),
    ADMIN_ADD("admin_add", "添加admin", true),
    ADMIN_DEL("admin_del", "删除admin", true),
    ADMIN_GETGOOGLEKEYEWM("admin_get_googlekey", "读取谷歌验证", true),

    ADMIN_UPDATE_BTC_RESULT_COUNT("admin_update_btc_result_count", "修改Game-BTC结果", true),

    COIN_EDIT_SETTLE_ADDRESS("Coin_edit_settle_address", "结算配置", true),
    COIN_DEFI_MINING("coin_defi_mining", "DeFi挖矿", true),
    COIN_DEFI_STAKING("coin_defi_staking", "质押挖矿", true),
    COIN_DEFI_VOUCHER("coin_defi_voucher", "DeFi代金", true),
    PAY_CHANNEL_EDIT("Pay_channel_edit", "出款配置", true),

    ;
    private String key;
    private boolean isAdmin;
    private String name;

    WebEventLogType(String key, String name, boolean isAdmin)
    {
        this.key = key;
        this.isAdmin = isAdmin;
        this.name = name;
    }

    public String getKey() {
        return key;
    }


    public String getName() {
        return name;
    }


    public boolean isAdmin() {
        return isAdmin;
    }

    public static WebEventLogType getType(String key)
    {
        WebEventLogType[] values = WebEventLogType.values();
        for(WebEventLogType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }


    public static void addModel(Model model)
    {
        model.addAttribute("webEventLogTypeArr", WebEventLogType.values());
    }

}
