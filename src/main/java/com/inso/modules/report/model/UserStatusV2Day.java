package com.inso.modules.report.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
import com.inso.modules.passport.gift.model.GiftPeriodType;
import com.inso.modules.passport.gift.model.GiftTargetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class UserStatusV2Day {

    /**
     log_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     log_userid                int(11) NOT NULL comment 'userid-受益人',
     log_username              varchar(255) NOT NULL comment  '受益人用户名',

     log_agentid               int(11) NOT NULL,
     log_agentname             varchar(255) NOT NULL ,
     log_staffid               int(11) NOT NULL ,
     log_staffname             varchar(255) NOT NULL ,

     log_total_lv1_count       int(11) NOT NULL DEFAULT 0 comment '1级人数总数',
     log_total_lv2_count       int(11) NOT NULL DEFAULT 0 comment '2级人数总数',

     log_return_lv1_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '',
     log_return_lv2_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '',

     log_valid_lv1_count       int(11) NOT NULL DEFAULT 0 comment '有效1级人数总数',
     log_valid_lv2_count       int(11) NOT NULL DEFAULT 0 comment '有效2级人数总数',

     log_trade_amount_number   decimal(25,8) NOT NULL DEFAULT 0 comment 'number',
     log_trade_amount_small    decimal(25,8) NOT NULL DEFAULT 0 comment '小',
     log_trade_amount_big      decimal(25,8) NOT NULL DEFAULT 0 comment '大',
     log_trade_amount_odd      decimal(25,8) NOT NULL DEFAULT 0 comment '单',
     log_trade_amount_even     decimal(25,8) NOT NULL DEFAULT 0 comment '双',

     log_pdate                 date NOT NULL comment '日期',

     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private long userid;
    private String username;

    private long totalLv1ActiveCount;
    private BigDecimal totalLv1MemberBalance;
    private boolean existMemberbalance;

    private long totalLv1RechargeCount;
    private BigDecimal totalLv1RechargeAmount;

    private long totalLv1WithdrawCount;
    private BigDecimal totalLv1WithdrawAmount;
    private BigDecimal totalLv1WithdrawFeemoney;

    private long totalLv1Count;
    private long totalLv2Count;

    private BigDecimal returnLv1Amount = BigDecimal.ZERO;
    private BigDecimal returnLv2Amount = BigDecimal.ZERO;

    private BigDecimal returnFirstRechargeLv1Amount = BigDecimal.ZERO;
    private BigDecimal returnFirstRechargeLv2Amount = BigDecimal.ZERO;

    private long validLv1Count;
    private long validLv2Count;

    private BigDecimal tradeLv1Volumn = BigDecimal.ZERO;
    private BigDecimal tradeLv2Volumn = BigDecimal.ZERO;

    private BigDecimal tradeAmountNumber = BigDecimal.ZERO;
    private BigDecimal tradeAmountSmall = BigDecimal.ZERO;
    private BigDecimal tradeAmountBig = BigDecimal.ZERO;
    private BigDecimal tradeAmountOdd = BigDecimal.ZERO;
    private BigDecimal tradeAmountEven = BigDecimal.ZERO;


    public static String getColumnPrefix(){
        return "log";
    }

    public boolean verifyEmpty()
    {
        if(totalLv1Count > 0 || totalLv2Count > 0 || validLv1Count > 0 || validLv2Count > 0)
        {
            return false;
        }

        if(returnLv1Amount != null && returnLv1Amount.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(returnLv2Amount != null && returnLv2Amount.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(returnFirstRechargeLv1Amount != null && returnFirstRechargeLv1Amount.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(returnFirstRechargeLv2Amount != null && returnFirstRechargeLv2Amount.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(tradeAmountNumber != null && tradeAmountNumber.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(tradeAmountSmall != null && tradeAmountSmall.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(tradeAmountBig != null && tradeAmountBig.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(tradeAmountOdd != null && tradeAmountOdd.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(tradeAmountEven != null && tradeAmountEven.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(totalLv1MemberBalance != null && totalLv1MemberBalance.compareTo(BigDecimal.ZERO) > 0)
        {
            return false;
        }

        if(totalLv1RechargeCount > 0 || totalLv1WithdrawCount > 0)
        {
            return false;
        }

        return true;
    }

    public void initNotEmpty()
    {
        this.returnLv1Amount = BigDecimalUtils.getNotNull(this.returnLv1Amount);
        this.returnLv2Amount = BigDecimalUtils.getNotNull(this.returnLv2Amount);
        this.tradeLv1Volumn = BigDecimalUtils.getNotNull(this.tradeLv1Volumn);
        this.tradeLv2Volumn = BigDecimalUtils.getNotNull(this.tradeLv2Volumn);

        this.tradeAmountNumber = BigDecimalUtils.getNotNull(this.tradeAmountNumber);
        this.tradeAmountBig = BigDecimalUtils.getNotNull(this.tradeAmountBig);
        this.tradeAmountSmall = BigDecimalUtils.getNotNull(this.tradeAmountSmall);
        this.tradeAmountOdd = BigDecimalUtils.getNotNull(this.tradeAmountOdd);
        this.tradeAmountEven = BigDecimalUtils.getNotNull(this.tradeAmountEven);
    }

    public void merge(UserStatusV2Day tmpEntity)
    {
        this.totalLv1Count += tmpEntity.getTotalLv1Count();
        this.totalLv2Count += tmpEntity.getTotalLv2Count();

        this.validLv1Count += tmpEntity.getValidLv1Count();
        this.validLv2Count += tmpEntity.getValidLv2Count();

        if(tmpEntity.getReturnLv1Amount().compareTo(BigDecimal.ZERO) > 0)
        {
            this.returnLv1Amount = this.returnLv1Amount.add(tmpEntity.getReturnLv1Amount());
        }

        if(tmpEntity.getReturnLv2Amount().compareTo(BigDecimal.ZERO) > 0)
        {
            this.returnLv2Amount = this.returnLv2Amount.add(tmpEntity.getReturnLv2Amount());
        }

        if(tmpEntity.getTradeLv1Volumn().compareTo(BigDecimal.ZERO) > 0)
        {
            this.tradeLv1Volumn = this.tradeLv1Volumn.add(tmpEntity.getTradeLv1Volumn());
        }

        if(tmpEntity.getTradeLv2Volumn().compareTo(BigDecimal.ZERO) > 0)
        {
            this.tradeLv2Volumn = this.tradeLv2Volumn.add(tmpEntity.getTradeLv2Volumn());
        }

        // bet
        if(tmpEntity.getTradeAmountNumber().compareTo(BigDecimal.ZERO) > 0)
        {
            this.tradeAmountNumber = this.tradeAmountNumber.add(tmpEntity.getTradeAmountNumber());
        }

        if(tmpEntity.getTradeAmountBig().compareTo(BigDecimal.ZERO) > 0)
        {
            this.tradeAmountBig = this.tradeAmountBig.add(tmpEntity.getTradeAmountBig());
        }

        if(tmpEntity.getTradeAmountSmall().compareTo(BigDecimal.ZERO) > 0)
        {
            this.tradeAmountSmall = this.tradeAmountSmall.add(tmpEntity.getTradeAmountSmall());
        }

        if(tmpEntity.getTradeAmountOdd().compareTo(BigDecimal.ZERO) > 0)
        {
            this.tradeAmountOdd = this.tradeAmountOdd.add(tmpEntity.getTradeAmountOdd());
        }

        if(tmpEntity.getTradeAmountEven().compareTo(BigDecimal.ZERO) > 0)
        {

            this.tradeAmountEven = this.tradeAmountEven.add(tmpEntity.getTradeAmountEven());
        }
    }

    public void mergeCore(UserStatusV2Day tmpEntity)
    {
        if(tmpEntity.getTotalLv1ActiveCount() > 0)
        {
            this.totalLv1ActiveCount += tmpEntity.getTotalLv1ActiveCount();
        }

//        if(tmpEntity.getTotalLv1MemberBalance() != null && tmpEntity.getTotalLv1MemberBalance().compareTo(BigDecimal.ZERO) > 0)
//        {
//            this.totalLv1MemberBalance = getTotalLv1MemberBalance().add(tmpEntity.getTotalLv1MemberBalance());
//        }

        if(tmpEntity.getTotalLv1RechargeCount() > 0)
        {
            this.totalLv1RechargeCount += tmpEntity.getTotalLv1RechargeCount();
            this.totalLv1RechargeAmount = getTotalLv1RechargeAmount().add(tmpEntity.getTotalLv1RechargeAmount());
        }

        if(tmpEntity.getTotalLv1WithdrawCount() > 0)
        {
            this.totalLv1WithdrawCount += tmpEntity.getTotalLv1WithdrawCount();
            this.totalLv1WithdrawAmount = getTotalLv1WithdrawAmount().add(tmpEntity.getTotalLv1WithdrawAmount());
            this.totalLv1WithdrawFeemoney = getTotalLv1WithdrawFeemoney().add(tmpEntity.getTotalLv1WithdrawFeemoney());
        }
    }


    public void updateIfHashLatest(UserStatusV2Day tmpEntity)
    {
        if(tmpEntity.getTotalLv1Count() > this.totalLv1Count)
        {
            this.totalLv1Count = tmpEntity.getTotalLv1Count();
        }

        if(tmpEntity.getTotalLv2Count() > this.totalLv2Count)
        {
            this.totalLv2Count = tmpEntity.getTotalLv2Count();
        }

        if(tmpEntity.getValidLv1Count() > this.validLv1Count)
        {
            this.validLv1Count = tmpEntity.getValidLv1Count();
        }

        if(tmpEntity.getValidLv2Count() > this.validLv2Count)
        {
            this.validLv2Count = tmpEntity.getValidLv2Count();
        }


        // return water
        if(tmpEntity.getReturnLv1Amount().compareTo(this.returnLv1Amount) > 0)
        {
            this.returnLv1Amount = tmpEntity.getReturnLv1Amount();
        }

        if(tmpEntity.getReturnLv2Amount().compareTo(this.returnLv2Amount) > 0)
        {
            this.returnLv2Amount = tmpEntity.getReturnLv2Amount();
        }

        // trade volumn
        if(tmpEntity.getTradeLv1Volumn().compareTo(this.tradeLv1Volumn) > 0)
        {
            this.tradeLv1Volumn = tmpEntity.getTradeLv1Volumn();
        }

        if(tmpEntity.getTradeLv2Volumn().compareTo(this.tradeLv2Volumn) > 0)
        {
            this.tradeLv2Volumn = tmpEntity.getTradeLv2Volumn();
        }


    }

    public void increTotalLvCount(boolean isLv1)
    {
        if(isLv1)
        {
            totalLv1Count ++;
        }
        else
        {
            totalLv2Count ++;
        }
    }

    public void increValidCount(boolean isLv1)
    {
        if(isLv1)
        {
            validLv1Count ++;
        }
        else
        {
            validLv2Count ++;
        }
    }

    public void increAmount(boolean isLv1, BigDecimal amount)
    {
        if(amount == null)
        {
            return;
        }

        if(isLv1)
        {
            this.returnLv1Amount = this.returnLv1Amount.add(amount).setScale(2, BigDecimal.ROUND_DOWN);
        }
        else
        {
            this.returnLv2Amount = this.returnLv2Amount.add(amount).setScale(2, BigDecimal.ROUND_DOWN);
        }
    }

    public void loadTradeData(DateTime dateTime, String useranme)
    {
        GiftStatusHelper helper = GiftStatusHelper.getInstance();
        GiftPeriodType periodType = GiftPeriodType.Day;
        setTradeAmountNumber(helper.getAmount(periodType, dateTime, useranme, GiftTargetType.BET_NUMBER.getKey()));
        setTradeAmountBig(helper.getAmount(periodType, dateTime, useranme, GiftTargetType.BET_BIG.getKey()));
        setTradeAmountSmall(helper.getAmount(periodType, dateTime, useranme, GiftTargetType.BET_SMALL.getKey()));
        setTradeAmountOdd(helper.getAmount(periodType, dateTime, useranme, GiftTargetType.BET_ODD.getKey()));
        setTradeAmountEven(helper.getAmount(periodType, dateTime, useranme, GiftTargetType.BET_EVEN.getKey()));
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public long getStaffid() {
        return staffid;
    }

    public void setStaffid(long staffid) {
        this.staffid = staffid;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
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

    public long getTotalLv1Count() {
        return totalLv1Count;
    }

    public void setTotalLv1Count(long totalLv1Count) {
        this.totalLv1Count = totalLv1Count;
    }

    public long getTotalLv2Count() {
        return totalLv2Count;
    }

    public void setTotalLv2Count(long totalLv2Count) {
        this.totalLv2Count = totalLv2Count;
    }

    public BigDecimal getReturnLv1Amount() {
        return BigDecimalUtils.getNotNull(returnLv1Amount);
    }

    public void setReturnLv1Amount(BigDecimal returnLv1Amount) {
        this.returnLv1Amount = returnLv1Amount;
    }

    public BigDecimal getReturnLv2Amount() {
        return BigDecimalUtils.getNotNull(returnLv2Amount);
    }

    public void setReturnLv2Amount(BigDecimal returnLv2Amount) {
        this.returnLv2Amount = returnLv2Amount;
    }

    public long getValidLv1Count() {
        return validLv1Count;
    }

    public void setValidLv1Count(long validLv1Count) {
        this.validLv1Count = validLv1Count;
    }

    public long getValidLv2Count() {
        return validLv2Count;
    }

    public void setValidLv2Count(long validLv2Count) {
        this.validLv2Count = validLv2Count;
    }

    public BigDecimal getTradeAmountNumber() {
        return BigDecimalUtils.getNotNull(tradeAmountNumber);
    }

    public void setTradeAmountNumber(BigDecimal tradeAmountNumber) {
        this.tradeAmountNumber = tradeAmountNumber;
    }

    public BigDecimal getTradeAmountSmall() {
        return BigDecimalUtils.getNotNull(tradeAmountSmall);
    }

    public void setTradeAmountSmall(BigDecimal tradeAmountSmall) {
        this.tradeAmountSmall = tradeAmountSmall;
    }

    public BigDecimal getTradeAmountBig() {
        return BigDecimalUtils.getNotNull(tradeAmountBig);
    }

    public void setTradeAmountBig(BigDecimal tradeAmountBig) {
        this.tradeAmountBig = tradeAmountBig;
    }

    public BigDecimal getTradeAmountOdd() {
        return BigDecimalUtils.getNotNull(tradeAmountOdd);
    }

    public void setTradeAmountOdd(BigDecimal tradeAmountOdd) {
        this.tradeAmountOdd = tradeAmountOdd;
    }

    public BigDecimal getTradeAmountEven() {
        return BigDecimalUtils.getNotNull(tradeAmountEven);
    }

    public void setTradeAmountEven(BigDecimal tradeAmountEven) {
        this.tradeAmountEven = tradeAmountEven;
    }


    public BigDecimal getTradeLv1Volumn() {
        return BigDecimalUtils.getNotNull(tradeLv1Volumn);
    }

    public void setTradeLv1Volumn(BigDecimal tradeLv1Volumn) {
        this.tradeLv1Volumn = tradeLv1Volumn;
    }

    public BigDecimal getTradeLv2Volumn() {
        return BigDecimalUtils.getNotNull(tradeLv2Volumn);
    }

    public void setTradeLv2Volumn(BigDecimal tradeLv2Volumn) {
        this.tradeLv2Volumn = tradeLv2Volumn;
    }

    public long getTotalLv1ActiveCount() {
        return totalLv1ActiveCount;
    }

    public void setTotalLv1ActiveCount(long totalLv1ActiveCount) {
        this.totalLv1ActiveCount = totalLv1ActiveCount;
    }

    public BigDecimal getTotalLv1MemberBalance() {
        return BigDecimalUtils.getNotNull(totalLv1MemberBalance);
    }

    public void setTotalLv1MemberBalance(BigDecimal totalLv1MemberBalance) {
        this.totalLv1MemberBalance = totalLv1MemberBalance;
    }

    public long getTotalLv1RechargeCount() {
        return totalLv1RechargeCount;
    }

    public void setTotalLv1RechargeCount(long totalLv1RechargeCount) {
        this.totalLv1RechargeCount = totalLv1RechargeCount;
    }

    public BigDecimal getTotalLv1RechargeAmount() {
        return BigDecimalUtils.getNotNull(totalLv1RechargeAmount);
    }

    public void setTotalLv1RechargeAmount(BigDecimal totalLv1RechargeAmount) {
        this.totalLv1RechargeAmount = totalLv1RechargeAmount;
    }

    public long getTotalLv1WithdrawCount() {
        return totalLv1WithdrawCount;
    }

    public void setTotalLv1WithdrawCount(long totalLv1WithdrawCount) {
        this.totalLv1WithdrawCount = totalLv1WithdrawCount;
    }

    public BigDecimal getTotalLv1WithdrawAmount() {
        return BigDecimalUtils.getNotNull(totalLv1WithdrawAmount);
    }

    public void setTotalLv1WithdrawAmount(BigDecimal totalLv1WithdrawAmount) {
        this.totalLv1WithdrawAmount = totalLv1WithdrawAmount;
    }

    public BigDecimal getTotalLv1WithdrawFeemoney() {
        return BigDecimalUtils.getNotNull(totalLv1WithdrawFeemoney);
    }

    public void setTotalLv1WithdrawFeemoney(BigDecimal totalLv1WithdrawFeemoney) {
        this.totalLv1WithdrawFeemoney = totalLv1WithdrawFeemoney;
    }

    public boolean isExistMemberbalance() {
        return existMemberbalance;
    }

    public void setExistMemberbalance(boolean existMemberbalance) {
        this.existMemberbalance = existMemberbalance;
    }

    public BigDecimal getReturnFirstRechargeLv1Amount() {
        return BigDecimalUtils.getNotNull(returnFirstRechargeLv1Amount);
    }

    public void setReturnFirstRechargeLv1Amount(BigDecimal returnFirstRechargeLv1Amount) {
        this.returnFirstRechargeLv1Amount = returnFirstRechargeLv1Amount;
    }

    public BigDecimal getReturnFirstRechargeLv2Amount() {
        return BigDecimalUtils.getNotNull(returnFirstRechargeLv2Amount);
    }

    public void setReturnFirstRechargeLv2Amount(BigDecimal returnFirstRechargeLv2Amount) {
        this.returnFirstRechargeLv2Amount = returnFirstRechargeLv2Amount;
    }

    public void handleToSafeData(BigDecimal lv1Rate, BigDecimal lv2Rate)
    {
        BigDecimal feeRate = BetFeemoneyHelper.getFeeRate();
        if(feeRate == null || feeRate.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        if(lv1Rate != null && lv1Rate.compareTo(BigDecimal.ZERO) > 0 && getReturnLv1Amount().compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal value = getReturnLv1Amount().divide(lv1Rate, 0, RoundingMode.DOWN);
            value = value.divide(feeRate, 0, RoundingMode.DOWN);
            this.setTradeLv1Volumn(value);
        }

        if(lv2Rate != null && lv2Rate.compareTo(BigDecimal.ZERO) > 0 && getReturnLv2Amount().compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal value = getReturnLv2Amount().divide(lv2Rate, 0, RoundingMode.DOWN);
            value = value.divide(feeRate, 0, RoundingMode.DOWN);
            this.setTradeLv2Volumn(value);
        }

    }

    public static void main(String[] args) {
        UserStatusV2Day entity = new UserStatusV2Day();
        entity.setTradeLv1Volumn(new BigDecimal(100));
        entity.setTradeLv2Volumn(new BigDecimal(200));

        entity.setReturnLv1Amount(new BigDecimal(9));
        entity.setReturnLv2Amount(new BigDecimal(5));

        entity.handleToSafeData(new BigDecimal(0.3), new BigDecimal(0.1));

        FastJsonHelper.prettyJson(entity);

        // 1000 * 0.03 * 0.3 = 9

    }
}

