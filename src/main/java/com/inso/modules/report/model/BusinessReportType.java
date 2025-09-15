package com.inso.modules.report.model;

public enum BusinessReportType {


    COIN_APPROVE("coin_approve", "正常关注"),


    GAME_PG("game_pg", "PG游戏"),

    ;

    private String key;
    private String name;

    BusinessReportType(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static BusinessReportType getType(String key)
    {
        BusinessReportType[] values = BusinessReportType.values();
        for(BusinessReportType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

}
