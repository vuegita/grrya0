package com.inso.modules.passport.gift.model;

public enum GiftPeriodType {

    Day("Day"),
    Week("Week"),
    ;

    private String key;

    public static final GiftPeriodType[] mArr = GiftPeriodType.values();

    GiftPeriodType(String key)
    {
        this.key = key;
    }


    public String getKey()
    {
        return key;
    }

    public static GiftPeriodType[] getArray()
    {
        return mArr;
    }

    public static GiftPeriodType getType(String key)
    {
        for(GiftPeriodType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
