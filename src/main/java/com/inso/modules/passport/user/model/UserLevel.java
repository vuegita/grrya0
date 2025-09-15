package com.inso.modules.passport.user.model;

/**
 * 用户等级
 */
public enum UserLevel {
    BAD("bad"),
    NORMAL("normal"),
    MIDDLE("middle"),
    HIGH("high"),
    ;

    private String key;

    UserLevel(String key)
    {
        this.key = key;
    }


    public String getKey()
    {
        return key;
    }

    public static UserLevel getType(String key)
    {
        UserLevel[] values = UserLevel.values();
        for(UserLevel type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
