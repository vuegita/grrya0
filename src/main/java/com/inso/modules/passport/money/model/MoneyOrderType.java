package com.inso.modules.passport.money.model;

/**
 * 订单类型
 */
public enum MoneyOrderType {

    PLATFORM_RECHARGE("platform_recharge", "系统充值"),
    PLATFORM_PRESENTATION("platform_presentation", "系统赠送"),
    PLATFORM_DEDUCT("platform_deduct", "系统扣款"),

    USER_RECHARGE("user_recharge", "用户充值"),
    USER_WITHDRAW("user_withdraw", "用户提现"),

    BUSINESS_RECHARGE("business_recharge", "业务充值"),
    BUSINESS_DEDUCT("business_deduct", "业务扣款"),

    FINANCE_RECHARGE("finance_recharge", "理财充值"),
    FINANCE_DEDUCT("finance_deduct", "理财扣款"),

    REFUND("refund", "退款"),

    RETURN_WATER("return_water", "返佣"),
    ;

    private String key;
    private String name;

    MoneyOrderType(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public static MoneyOrderType getType(String key)
    {
        MoneyOrderType[] values = MoneyOrderType.values();
        for(MoneyOrderType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

}
