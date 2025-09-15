package com.inso.modules.ad.core.model;

public enum  AdEventType {

    DOWNLOAD("download", "下载"),
    BUY("buy", "购买"),
    LIKE("like", "点赞"),
    VIDEO("video", "视频"),
    SHOP("shop", "商品"),
    ;

    private static final String CONFIG_KEY = "system.support.currency";

    private String key;
    private String name;

    AdEventType(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public String getName() {
        return name;
    }

    public static AdEventType getType(String key)
    {
        AdEventType[] values = AdEventType.values();
        for(AdEventType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
