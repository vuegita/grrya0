package com.inso.modules.paychannel.model;

public enum ChannelType {

    PAYIN("payin"),
    PAYOUT("payout"),

    ;

    private String key;

    ChannelType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ChannelType getType(String key)
    {
        ChannelType[] values = ChannelType.values();
        for(ChannelType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
