package com.inso.modules.websocket.model;

public enum MyGroupType {

    GAME_ROCKET("game_rocket"),
    GAME_FOOTBALL("game_football"),
    GAME_MINES("game_mines"),

    HALL("hall"),

    ;

    public static final MyGroupType[] mArr = MyGroupType.values();

    private String key;


    /**
     *
     * @param key
     */
    MyGroupType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static MyGroupType getType(String key)
    {
        for(MyGroupType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
