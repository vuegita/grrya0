package com.inso.modules.web.model;

/**
 * 系统关注用户类型
 */
public enum VIPType {

    AD("ad", "广告"),

    ;

    private String key;
    private String name;

    VIPType(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static VIPType getType(String key)
    {
        VIPType[] values = VIPType.values();
        for(VIPType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
