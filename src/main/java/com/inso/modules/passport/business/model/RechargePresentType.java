package com.inso.modules.passport.business.model;

import com.inso.modules.web.team.model.TeamBusinessType;

/**
 * 充值赠送类型
 */
public enum RechargePresentType {

//    RedP("redp"), // 红包充值赠送

    WEB_TEAM_BUY_USER_RECHARGE_CREATE_GROUP(TeamBusinessType.USER_RECHARGE.getKey()  + "_create_group"), // 创建拼团分组  //web_team_buy_user_recharge_create_group
    WEB_TEAM_BUY_USER_RECHARGE_JOIN_GROUP(TeamBusinessType.USER_RECHARGE.getKey()  + "_join_group"), // 参加拼团  //web_team_buy_user_recharge_join_group

    ;

    private String key;

    private RechargePresentType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static RechargePresentType getType(String key)
    {
        RechargePresentType[] values = RechargePresentType.values();
        for(RechargePresentType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
