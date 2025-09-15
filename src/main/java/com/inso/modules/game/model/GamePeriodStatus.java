package com.inso.modules.game.model;

public enum GamePeriodStatus {

    PENDING("pending"),
    WAITING("waiting"),
    FINISH("finish"),
            ;

    private String key;
    GamePeriodStatus(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }


    public static GamePeriodStatus getType(String key)
    {
        GamePeriodStatus[] values = GamePeriodStatus.values();
        for(GamePeriodStatus type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
