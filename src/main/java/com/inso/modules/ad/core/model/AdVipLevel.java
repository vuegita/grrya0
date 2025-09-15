package com.inso.modules.ad.core.model;

import java.math.BigDecimal;

/**
 * 广告VIP等级
 */
public enum AdVipLevel {

    FREE("Free", 0, 0, 0, 3, 3),
    BASIC("Basic", 1, 1500, 50, 3, 3),
    BASIC_PLUS("Basic+", 2, 6000, 50, 3, 3),
    PRO("Pro", 3, 24000, 40, 3, 3),
    PRO_PLUS("Pro+", 4, 48000, 40, 3, 3),
    PREMIUM("Premium", 5, 90000, 30, 3, 3),
    PREMIUM_PLUS("Premium+", 6, 120000, 30, 3, 3),

    ;

    private String key;
    private long level;
    /*** 价格 ***/
    private BigDecimal price;
    // 回本周期
    private long paybackPeriod;
    /*** 邀请好友个数/每天 ***/
    private long inviteCountOfDay;
    /*** buy vip 个数 ***/
    private long buyCountOfDay;

    AdVipLevel(String key, long level, int price, long paybackPeriod, long inviteCountOfDay, long buyCountOfDay)
    {
        this.key = key;
        this.level = level;
        this.price = new BigDecimal(price);
        this.paybackPeriod = paybackPeriod;
        this.inviteCountOfDay = inviteCountOfDay;
    }

    public String getKey() {
        return key;
    }

    public long getLevel() {
        return level;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getPaybackPeriod() {
        return paybackPeriod;
    }

    public long getInviteCountOfDay() {
        return inviteCountOfDay;
    }

    public long getBuyCountOfDay() {
        return buyCountOfDay;
    }

    public static AdVipLevel getType(String key)
    {
        AdVipLevel[] values = AdVipLevel.values();
        for(AdVipLevel type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
