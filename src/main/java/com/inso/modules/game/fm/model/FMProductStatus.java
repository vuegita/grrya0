package com.inso.modules.game.fm.model;

public enum FMProductStatus {
    NEW("new"), // 草稿
    SALING("saling"), // 销售中
    SALED("saled"), // 已售磬
    REALIZED("realized"),// 结束
    DISCARD("discard"),// 丢弃
    ;

    private String key;

    FMProductStatus(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static FMProductStatus getType(String key)
    {
        FMProductStatus[] values = FMProductStatus.values();
        for(FMProductStatus type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
