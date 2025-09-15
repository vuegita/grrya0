package com.inso.modules.websocket.model;

public enum MyRoomType {

    // 游戏相关
    GAME_ROCKET("game_rocket"),
    GAME_TURNTABLE("game_turntable"),
    GAME_RED_GREEN2("game_red_green2"),


    // 大厅
    HALL("hall"),




    ;

    public static final MyRoomType[] mArr = MyRoomType.values();

    private String key;


    /**
     *
     * @param key
     */
    MyRoomType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static MyRoomType getType(String key)
    {
        for(MyRoomType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
