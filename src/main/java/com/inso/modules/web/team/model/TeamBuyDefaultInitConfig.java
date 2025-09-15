package com.inso.modules.web.team.model;

import com.inso.modules.common.model.FiatCurrencyType;
import com.inso.modules.common.model.ICurrencyType;

import java.math.BigDecimal;

public enum TeamBuyDefaultInitConfig {


    LV1(1,1,  new BigDecimal(0.005), 100, "0.01"),
    LV2(2,2,  new BigDecimal(0.006), 200, "0.01,0.02"),
    LV3(3,3,  new BigDecimal(0.008), 500, "0.01,0.02,0.03"),
    LV4(4,5,  new BigDecimal(0.01), 1000, "0.01,0.02,0.03,0.04,0.05"),
    LV5(5,7,  new BigDecimal(0.012), 2000, "0.01,0.02,0.03,0.04,0.05,0.07,0.09"),
    LV6(6,9,  new BigDecimal(0.015), 3500, "0.01,0.02,0.03,0.04,0.05,0.07,0.09,0.12,0.15"),
    LV7(7,10, new BigDecimal(0.02), 5000, "0.01,0.02,0.03,0.04,0.05,0.07,0.09,0.12,0.15,0.23"),


    ;
    private long level;
    private long inviteCount;
    private String returnCreateRate;
    private BigDecimal returnJoinRate;
    private BigDecimal limitMinInvesAmount;

    TeamBuyDefaultInitConfig(long level, long inviteCount, BigDecimal returnJoinRate, int invesAmount, String returnCreateRate)
    {
        this.level = level;
        this.inviteCount = inviteCount;
        this.returnCreateRate = returnCreateRate;
        this.returnJoinRate = returnJoinRate;


        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        if(currencyType == FiatCurrencyType.INR)
        {
            this.limitMinInvesAmount = new BigDecimal(invesAmount * 10);
        }
        else
        {
            this.limitMinInvesAmount = new BigDecimal(invesAmount);
        }
    }

    public long getLevel() {
        return level;
    }

    public long getInviteCount() {
        return inviteCount;
    }

    public String getReturnCreateRate() {
        return returnCreateRate;
    }

    public BigDecimal getReturnJoinRate() {
        return returnJoinRate;
    }

    public BigDecimal getLimitMinInvesAmount() {
        return limitMinInvesAmount;
    }
}
