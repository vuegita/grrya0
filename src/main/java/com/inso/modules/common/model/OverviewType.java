package com.inso.modules.common.model;

public enum OverviewType {

    PLATFORM_STATS("platform_stats", false), // 平台汇总 - 总充值|总提现
    AGENT_STATS_TODAY("agent_stats_today", false), // 代理 - 总充值|总提现

    USER_COUNT("user_count", false), // 会员总数统计
    USER_AMOUNT("user_amount", false), // 会员金额统计

    AGENT_SUB_COUNT("agent_sub_count", false), // 代理下级会员相关总数统计
//    AGENT_SUB_AMOUNT("agent_sub_amount"), // 代理下级会员金额统计

    GAME_LOTTERY_RG("game_lottery_rg", true), // 游戏红绿汇总
    GAME_ANDAR_BAHAR("game_andar_bahar", true), // 游戏AB汇总

    GAME_FRUIT("game_fruit", true), // 游戏水果机汇总

    GAME_BTC_KLINE("game_btc_kline", false), //
    GAME_RED_GREEN("game_red_green", false), //
    GAME_TURNTABLE("game_turntable", false), //
    GAME_ROCKET("game_rocket", false), //
    GAME_FOOTBALL("game_football", false), //
    GAME_MINES("game_mines", false), //
    GAME_PG("game_pg", false), //
    ;

    private String key;
    private boolean disable;

    private OverviewType(String key, boolean disable)
    {
        this.key = key;
        this.disable = disable;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isDisable() {
        return disable;
    }

    public static OverviewType getType(String key)
    {
        OverviewType[] values = OverviewType.values();
        for(OverviewType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
