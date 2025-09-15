package com.inso.modules.coin.core.model;

public enum MyDimensionType {

    PROJECT("Project", "项目方"),
    PLATFORM("Platform", "平台"),
    AGENT("Agent", "代理"),
//        MEMBER("member"),

    ;

    private String key;
    private String name;

    MyDimensionType(String key, String name)
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

    public static MyDimensionType getType(String key)
    {
        MyDimensionType[] values = MyDimensionType.values();
        for(MyDimensionType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
