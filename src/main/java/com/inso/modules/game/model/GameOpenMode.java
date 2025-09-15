package com.inso.modules.game.model;


public enum GameOpenMode {

    RANDOM("random"), // 随机开奖
    MANUAL("manual"), // 手动开奖
    RATE("rate"), // 计算开奖
    SMART("smart"), // 智能开奖
    ;

    private String key;
    GameOpenMode(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }


    public static GameOpenMode getType(String key)
    {
        GameOpenMode[] values = GameOpenMode.values();
        for(GameOpenMode type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }


}
