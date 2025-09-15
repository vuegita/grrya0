package com.inso.modules.passport.returnwater.model;

import java.math.BigDecimal;

public class ReturnWaterLog {

    private long userid;
    private String username;

    private String fundkey;
    private String currency;

    private BigDecimal level1Amount;
    private long level1Count;

    private BigDecimal level2Amount;
    private long level2Count;

    /*** 总充值金额 ***/
    private BigDecimal level1TotalRecharge;

    public static String getColumnPrefix(){
        return "log";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getLevel1TotalRecharge() {
        return level1TotalRecharge;
    }

    public void setLevel1TotalRecharge(BigDecimal level1TotalRecharge) {
        this.level1TotalRecharge = level1TotalRecharge;
    }

    public BigDecimal getLevel1Amount() {
        return level1Amount;
    }

    public void setLevel1Amount(BigDecimal level1Amount) {
        this.level1Amount = level1Amount;
    }

    public long getLevel1Count() {
        return level1Count;
    }

    public void setLevel1Count(long level1Count) {
        this.level1Count = level1Count;
    }

    public BigDecimal getLevel2Amount() {
        return level2Amount;
    }

    public void setLevel2Amount(BigDecimal level2Amount) {
        this.level2Amount = level2Amount;
    }

    public long getLevel2Count() {
        return level2Count;
    }

    public void setLevel2Count(long level2Count) {
        this.level2Count = level2Count;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getFundkey() {
        return fundkey;
    }

    public void setFundkey(String fundkey) {
        this.fundkey = fundkey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
