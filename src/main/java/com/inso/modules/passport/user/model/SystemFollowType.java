package com.inso.modules.passport.user.model;

/**
 * 系统关注用户类型
 */
public enum SystemFollowType {


    SIMPLE("simple", "正常关注"),
    ERR_BET("err_bet", "投注异常"),

    ;

    private String key;
    private String title;

    SystemFollowType(String key, String title)
    {
        this.key = key;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public static SystemFollowType getType(String key)
    {
        SystemFollowType[] values = SystemFollowType.values();
        for(SystemFollowType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
