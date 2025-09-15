package com.inso.modules.passport.money.model;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

public class UserMoney {


    public static final BigDecimal CODE_2_BALANCE_MULTIPLE = BigDecimalUtils.DEF_10;

    private long id;
    private long userid;
    private String username;

    private String fundKey;
    private String currency;

    private BigDecimal balance;
    private BigDecimal freeze;
    private BigDecimal codeAmount;
    /*** 总打码-不扣除 ***/
    private BigDecimal totalDeductCodeAmount;
    /*** 冷钱包-类似冻结概念-但是这个金额已经加到用户余额里，只是不可用，因此需要用户自行扣除 ***/
    private BigDecimal coldAmount;

    private BigDecimal limitAmount;
    private BigDecimal limitCode;

    private BigDecimal totalRecharge = BigDecimal.ZERO;
    private BigDecimal totalWithdraw = BigDecimal.ZERO;
    private BigDecimal totalRefund = BigDecimal.ZERO;

    private transient long levelCount;

    public static String getColumnPrefix(){
        return "money";
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFreeze() {
        return freeze;
    }

    public void setFreeze(BigDecimal freeze) {
        this.freeze = freeze;
    }

    @JSONField(serialize = false,deserialize = false)
    public boolean verify(BigDecimal deductAmount)
    {
        BigDecimal rsAmount = balance.subtract(deductAmount).subtract(freeze);
        if(coldAmount != null)
        {
            // 冷钱包金额
            rsAmount = rsAmount.subtract(coldAmount);
        }
        return rsAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    @JSONField(serialize = false,deserialize = false)
    public boolean verifyWithdraw(BigDecimal deductAmount)
    {
        BigDecimal rsAmount = balance.subtract(deductAmount).subtract(freeze);
        if(coldAmount != null && coldAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            // 冷钱包金额
            rsAmount = rsAmount.subtract(coldAmount);
        }
        if(codeAmount != null && codeAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            rsAmount = rsAmount.subtract(codeAmount);
        }
        if(limitAmount != null && limitAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            rsAmount = rsAmount.subtract(limitAmount);
        }
        return rsAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    @JSONField(serialize = false,deserialize = false)
    public BigDecimal getValidBalance()
    {
        BigDecimal rsAmount = balance;

        if(coldAmount != null)
        {
            if(rsAmount.compareTo(coldAmount) <= 0)
            {
                coldAmount = rsAmount;
            }
            // 冷钱包金额
            rsAmount = rsAmount.subtract(coldAmount);
        }

        //繁殖数据异常
        if(rsAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            rsAmount = BigDecimal.ZERO;
        }
        return rsAmount;
    }

    @JSONField(serialize = false,deserialize = false)
    public BigDecimal getValidWithdrawBalance()
    {
        BigDecimal rsAmount = getValidBalance();
        if(limitAmount != null)
        {
            rsAmount = rsAmount.subtract(limitAmount);
        }
        if(rsAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            rsAmount = BigDecimal.ZERO;
        }
        return rsAmount;
    }



    public BigDecimal getCodeAmount() {
        return BigDecimalUtils.getNotNull(codeAmount);
    }

    public void setCodeAmount(BigDecimal codeAmount) {
        this.codeAmount = codeAmount;
    }

    public BigDecimal getColdAmount() {
        return coldAmount;
    }

    public void setColdAmount(BigDecimal coldAmount) {
        this.coldAmount = coldAmount;
    }

    public BigDecimal getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(BigDecimal totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public BigDecimal getTotalWithdraw() {
        return BigDecimalUtils.getNotNull(totalWithdraw);
    }

    public void setTotalWithdraw(BigDecimal totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

    public BigDecimal getTotalRefund() {
        return BigDecimalUtils.getNotNull(totalRefund);
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFundKey() {
        return fundKey;
    }

    public void setFundKey(String fundKey) {
        this.fundKey = fundKey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalDeductCodeAmount() {
        return BigDecimalUtils.getNotNull(totalDeductCodeAmount);
    }

    public void setTotalDeductCodeAmount(BigDecimal totalDeductCodeAmount) {
        this.totalDeductCodeAmount = totalDeductCodeAmount;
    }

    public BigDecimal getLimitAmount() {
        return BigDecimalUtils.getNotNull(limitAmount);
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getLimitCode() {
        return BigDecimalUtils.getNotNull(limitCode);
    }

    public void setLimitCode(BigDecimal limitCode) {
        this.limitCode = limitCode;
    }

    public long getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(long levelCount) {
        this.levelCount = levelCount;
    }
}
