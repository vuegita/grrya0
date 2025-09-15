package com.inso.modules.websocket.model;

public enum MyEventType {

    // 游戏相关
    GAME_STATUS_GET_LOTTERY("getLotteryStatus"),
    GAME_STATUS_GET_LATEST_BET_RECORD("getLatestBetRecord"),
    GAME_STATUS_GET_USER_CURRENT_BET_RECORD("getUserCurrentBetRecord"),
    GAME_STATUS_GET_LATEST_PERIOD_LIST("getLatestPeriodList"),

    GAME_SUBMIT_ORDER("submitOrder"),
    GAME_CASHOUT_ORDER("cashoutOrder"), // 结算订单
    GAME_BET_ORDER_STEP("betOrderStep"),

    // 大厅相关
//    HALL_ADD_ONLINE_USER_COUNT("addOnlineUserCount"),
    HALL_JOIN_ROOM("hall_join_room"), // 加入
    HALL_GAME_GET_ALL_BET_RECORD("hall_game_getAllBetRecord"), // 加入
    HALL_WEB_GET_RANK_BIG_GEST_DATALIST("hall_web_getRankBigGestDataList"),

    HALL_GET_USER_INFO("hall_getUserInfo"), // 获取余额



    ;

    public static final MyEventType[] mArr = MyEventType.values();

    private String key;


    /**
     *
     * @param key
     */
    MyEventType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static MyEventType getType(String key)
    {
        for(MyEventType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
