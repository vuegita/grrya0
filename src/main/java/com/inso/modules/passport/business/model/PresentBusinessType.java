package com.inso.modules.passport.business.model;

public enum PresentBusinessType {

    DAY_RETURN_TO_SELF("DAY_RETURN_TO_SELF",true), // 每日赠送


    INVITE_DAY("day_invite",true), // 每日赠送
    INVITE_DAY_NO_NEED_RECHARGE("invite_day_no_need_recharge",true), // 每日赠送
    INVITE_WEEK("week_invite", false), // 每周赠送

    FIRST_RECHARGE_PRESENT_AMOUNT("first_recharge_present_amount", false) ,// 首次充值的金额赠送， 只赠送一次


    GAME_BET_DAY("game_bet_day", true), // 下单流水
    GAME_BET_WEEK("game_bet_week", false), // 下单流水

    ;

    private String key;
    private boolean onlyDay;

    PresentBusinessType(String key, boolean onlyDay)
    {
        this.key = key;
        this.onlyDay = onlyDay;
    }


    public String getKey()
    {
        return key;
    }

    public boolean isDayOnly()
    {
        return onlyDay;
    }

    public static PresentBusinessType getType(String key)
    {
        PresentBusinessType[] values = PresentBusinessType.values();
        for(PresentBusinessType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}
