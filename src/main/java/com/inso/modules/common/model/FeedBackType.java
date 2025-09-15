package com.inso.modules.common.model;

public enum FeedBackType {

    SUGGESTION("suggestion", "建议"),
    CONSULT("consult", "咨询"),
    RECHARGE("recharge", "充值问题"),
    WITHDRAW("withdraw", "提现问题"),
    ORDER("order", "订单问题"),
    OTHER("other", "其他"),
    STATION_LETTER("station_letter", "站内信"),

    ;

    private String key;
    private String name;

    FeedBackType(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public static FeedBackType getType(String key)
    {
        FeedBackType[] values = FeedBackType.values();
        for(FeedBackType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

}
