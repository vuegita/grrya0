package com.inso.modules.game.andar_bahar.model;

public enum ABBetItemType {

    ANDAR("Andar"), // 庄家
    BAHAR("Bahar"), // 闲家
    TIE("Tie"), // 和
    ;

    private String key;
    ABBetItemType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }


    public static ABBetItemType getType(String key)
    {
        ABBetItemType[] values = ABBetItemType.values();
        for(ABBetItemType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
