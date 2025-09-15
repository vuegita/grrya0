package com.inso.modules.game.model;

import com.inso.modules.common.model.OverviewType;

public enum GameCategory {
    LOTTERY_RG("lottery_rg", "WINGO", OverviewType.GAME_LOTTERY_RG),

    ANDAR_BAHAR("andar-bahar", "Andar-Bahar", OverviewType.GAME_ANDAR_BAHAR),
    RED_PACKAGE("red-package", "Red-package", null),
    FINANCIAL_MANAGEMENT("financial-management", "Financial-Management", null),
    TASK_CHECKIN("task_checkin", "Task-Checkin", null),
    Sicbo("sicbo_simple", "Sicbo", null),
    FRUIT("fruit_simple", "Fruit", OverviewType.GAME_FRUIT),


    // V2
    ROCKET("Rocket", "Rocket", OverviewType.GAME_ROCKET,  "inso_game_lottery_v2_rocket_period", "inso_game_lottery_v2_rocket_order"),
    TURNTABLE("turntable", "Turntable", OverviewType.GAME_TURNTABLE, "inso_game_lottery_v2_turntable_period", "inso_game_lottery_v2_turntable_order"),
    BTC_KLINE("btc_kline", "BTC-Spot", OverviewType.GAME_BTC_KLINE, "inso_game_lottery_v2_btc_kline_period", "inso_game_lottery_v2_btc_kline_order"),
    RED_GREEN("red_green2", "WINGO", OverviewType.GAME_RED_GREEN, "inso_game_lottery_v2_rg2_period", "inso_game_lottery_v2_rg2_order"),
    FOOTABALL("Football", "Football", OverviewType.GAME_FOOTBALL, null, "inso_game_lottery_v2_football_order"),
    Mines("Mines", "Mines", OverviewType.GAME_MINES, null, "inso_game_lottery_v2_mines_order"),
    PG("PG", "PG", OverviewType.GAME_PG, null, "inso_game_lottery_v2_pg_order"),
    ;

    private String key;
    private String name;

    private OverviewType mOverviewType;

    private String periodTable;
    private String orderTable;

    GameCategory(String key, String name, OverviewType type)
    {
        this.key = key;
        this.name = name;
        this.mOverviewType = type;
    }

    GameCategory(String key, String name, OverviewType type, String periodTable, String orderTable)
    {
        this.key = key;
        this.name = name;
        this.mOverviewType = type;
        this.periodTable = periodTable;
        this.orderTable = orderTable;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public OverviewType getOverviewType() {
        return mOverviewType;
    }

    public static GameCategory getType(String key)
    {
        GameCategory[] values = GameCategory.values();
        for(GameCategory type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public String getPeriodTable() {
        return periodTable;
    }

    public void setPeriodTable(String periodTable) {
        this.periodTable = periodTable;
    }

    public String getOrderTable() {
        return orderTable;
    }

    public void setOrderTable(String orderTable) {
        this.orderTable = orderTable;
    }
}
