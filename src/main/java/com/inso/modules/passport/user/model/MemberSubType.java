package com.inso.modules.passport.user.model;

public enum MemberSubType {

    SIMPLE("simple"),
    PROMOTION("promotion")
    ;

    private String key;

    private MemberSubType(String key)
    {
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    public static MemberSubType getType(String key)
    {
        MemberSubType[] values = MemberSubType.values();
        for(MemberSubType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

}
