package com.inso.modules.common.model;

public enum Status {

    WAITING("waiting"),
    FINISH("finish"),

    APPLY("apply"),

    ENABLE("enable"),
    DISABLE("disable"),
    FREEZE("freeze"),

    FUND_ENABLE("fund_enable"), // 资金账户类型状态 - 代理的钱包小号
    FUND_DISABLE("fund_disable"), // 资金账户类型状态-代理的钱包小号
    ;

    private String key;

    Status(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public static Status getType(String key)
    {
        Status[] values = Status.values();
        for(Status type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
