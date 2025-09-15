package com.inso.modules.passport.business.model;

public enum ReturnWaterType {

    GAME("game"),
    AD("ad"),
    COIN_DEFI("coin_defi"),
    COIN_CLOUD("coin_cloud"),

    COIN_BINANCE_ACTIVITY("coin_binance_activity"),
    ;

    private String key;

    ReturnWaterType(String key)
    {
        this.key = key;
    }


    public String getKey()
    {
        return key;
    }

    public static ReturnWaterType getType(String key)
    {
        ReturnWaterType[] values = ReturnWaterType.values();
        for(ReturnWaterType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
