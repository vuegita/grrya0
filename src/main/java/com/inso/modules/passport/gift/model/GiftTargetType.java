package com.inso.modules.passport.gift.model;

public enum GiftTargetType {

    BET_TURNOVER("bet_turnover", "下注总流水", false), //下注总流水

    BET_BIG("bet_big", "大", true),
    BET_SMALL("bet_small", "小", true),

    BET_ODD("bet_odd", "单", true), // 单
    BET_EVEN("bet_even", "双", true),  // 双

    BET_NUMBER("bet_number", "数字", true),
    ;

    private String key;
    private String name;
    private boolean onlyDay;
//    private BigDecimal limitMinValue;

    public static final GiftTargetType[] mArr = GiftTargetType.values();

    GiftTargetType(String key, String name, boolean onlyDay)
    {
        this.key = key;
        this.name = name;
        this.onlyDay = onlyDay;
    }

    public String getKey()
    {
        return key;
    }

    public String getName() {
        return name;
    }

    public boolean isOnlyDay() {
        return onlyDay;
    }

    //    public BigDecimal getLimitMinValue() {
//        return limitMinValue;
//    }

    public static GiftTargetType[] getArray()
    {
        return mArr;
    }

    public static GiftTargetType getType(String key)
    {
        for(GiftTargetType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
