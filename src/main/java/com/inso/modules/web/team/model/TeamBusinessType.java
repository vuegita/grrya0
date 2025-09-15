package com.inso.modules.web.team.model;

import org.springframework.ui.Model;

public enum TeamBusinessType {


//    COIN_CLOUD_MINING_STAKING("web_team_buy_coin_cloud_mining_staking", "云挖矿质押"),
    USER_RECHARGE("web_team_buy_user_recharge", "用户充值"),

    ;
    private String key;
    private String remark;

    TeamBusinessType(String key, String remark)
    {
        this.key = key;
        this.remark = remark;
    }

    public String getKey() {
        return key;
    }

    public String getRemark() {
        return remark;
    }

    public static TeamBusinessType getType(String key)
    {
        TeamBusinessType[] values = TeamBusinessType.values();
        for(TeamBusinessType type : values)
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
        model.addAttribute("teamBuyingBusinessArr", TeamBusinessType.values());
    }

}
