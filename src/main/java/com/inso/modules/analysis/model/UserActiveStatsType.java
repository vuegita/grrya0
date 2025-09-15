package com.inso.modules.analysis.model;

public enum UserActiveStatsType {

    // 在线总时长
    ONLINE_DURATION("online_duration"),

    // 红绿总时长
    STAY_RG_DURATION("stay_rg_duration"),

    // AB游戏
    STAY_AB_DURATION("stay_ab_duration"),

    // 水果机
    STAY_FRUIT_DURATION("stay_fruit_duration"),

    // 理财
    STAY_FM_DURATION("stay_fm_duration"),


    ;

    private String key;

    UserActiveStatsType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public static UserActiveStatsType getType(String key)
    {
        UserActiveStatsType[] values = UserActiveStatsType.values();
        for(UserActiveStatsType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
