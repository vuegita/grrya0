package com.inso.modules.common.model;

public enum BannerType {
//ad|game_ab|game_rg|game_fruit|game_fm|game_redpackage
    AD("ad"),
    GAMEAB("game_ab"),

    GAMERG("game_rg"),
    GAMEFRUIT("game_fruit"),
    GAMEFM("game_fm"),

    GAMEREDPACKAGE("game_redpackage"),

    ;

    private String key;

    BannerType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public static BannerType getType(String key)
    {
        BannerType[] values = BannerType.values();
        for(BannerType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
