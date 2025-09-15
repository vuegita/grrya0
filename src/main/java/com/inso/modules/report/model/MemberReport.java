package com.inso.modules.report.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;

public class MemberReport {

    /**
     *   day_pdate	 				date NOT NULL ,
     *   day_userid           		int(11) NOT NULL comment 'userid',
     *   day_username    			varchar(50) NOT NULL comment  '',
     *   day_recharge      		decimal(18,2) NOT NULL DEFAULT 0 comment '充值金额',
     *   day_refund		        decimal(18,2) NOT NULL DEFAULT 0 comment '退款金额-保留字段',
     *   day_withdraw      		decimal(18,2) NOT NULL DEFAULT 0 comment '提现金额',
     *   day_business_recharge    	decimal(18,2) NOT NULL DEFAULT 0 comment '业务充值-如中奖',
     *   day_business_deduct      	decimal(18,2) NOT NULL DEFAULT 0 comment '业务扣款-如投注',
     *   day_platform_recharge     decimal(18,2) NOT NULL DEFAULT 0 comment '平台充值',
     *   day_platform_presentation decimal(18,2) NOT NULL DEFAULT 0 comment '平台赠送',
     *   day_platform_deduct       decimal(18,2) NOT NULL DEFAULT 0 comment '平台扣款',
     *   day_feemoney             	decimal(18,2) NOT NULL DEFAULT 0 comment '手续费-提现才有',
     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long userid;
    private String username;

    private String fundKey;
    private String currency;

    private BigDecimal recharge;
    private BigDecimal withdraw;
    private BigDecimal refund;
    private BigDecimal businessRecharge;
    private BigDecimal businessDeduct;
    private BigDecimal businessFeemoney;
    private BigDecimal platformRecharge;
    private BigDecimal platformDeduct;
    private BigDecimal platformPresentation;
    private BigDecimal feemoney;

    private BigDecimal returnWater;

    private BigDecimal financeRecharge;
    private BigDecimal financeDeduct;
    private BigDecimal financeFeemoney;

    /*** 会员才有， 代理和员工没有 ***/
    private BigDecimal balance = BigDecimal.ZERO;

    /*** 盈亏总额 ***/
    private BigDecimal totalProfit;

    /*** 用户业务盈亏总额 ***/
    private BigDecimal totalBusinessProfitLoss = BigDecimal.ZERO;

    private BigDecimal lv1Recharge;
    private BigDecimal lv1Withdraw;
    private BigDecimal lv2Recharge;
    private BigDecimal lv2Withdraw;

    public static String getColumnPrefix(){
        return "day";
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
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

    public BigDecimal getRecharge() {
        return recharge;
    }

    public void setRecharge(BigDecimal recharge) {
        this.recharge = recharge;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }

    public BigDecimal getRefund() {
        return refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }

    public BigDecimal getBusinessRecharge() {
        return businessRecharge;
    }

    public void setBusinessRecharge(BigDecimal businessRecharge) {
        this.businessRecharge = businessRecharge;
    }

    public BigDecimal getBusinessDeduct() {
        return businessDeduct;
    }

    public void setBusinessDeduct(BigDecimal businessDeduct) {
        this.businessDeduct = businessDeduct;
    }

    public BigDecimal getPlatformRecharge() {
        return platformRecharge;
    }

    public void setPlatformRecharge(BigDecimal platformRecharge) {
        this.platformRecharge = platformRecharge;
    }

    public BigDecimal getPlatformDeduct() {
        return platformDeduct;
    }

    public void setPlatformDeduct(BigDecimal platformDeduct) {
        this.platformDeduct = platformDeduct;
    }

    public BigDecimal getPlatformPresentation() {
        return platformPresentation;
    }

    public void setPlatformPresentation(BigDecimal platformPresentation) {
        this.platformPresentation = platformPresentation;
    }

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }


    public void incre(MemberReport report)
    {
        // 用户
        this.recharge = this.recharge.add(report.getRecharge()) ;
        this.withdraw = this.withdraw.add(report.getWithdraw());
        this.feemoney = this.feemoney.add(report.getFeemoney());

        // 平台
        this.platformRecharge = this.platformRecharge.add(report.getPlatformRecharge());
        this.platformPresentation = this.platformPresentation.add(report.getPlatformPresentation());
        this.platformDeduct = this.platformDeduct.add(report.getPlatformDeduct());

        // 业务
        this.businessRecharge = this.businessRecharge.add(report.getBusinessRecharge());
        this.businessDeduct = this.businessDeduct.add(report.getBusinessDeduct());
        this.businessFeemoney = this.businessFeemoney.add(report.getBusinessFeemoney());

        // 理财
        this.financeRecharge = this.financeRecharge.add(report.getFinanceRecharge());
        this.financeDeduct = this.financeDeduct.add(report.getFinanceDeduct());
        this.financeFeemoney = this.financeFeemoney.add(report.getFinanceFeemoney());

        this.refund = this.refund.add(report.getRefund());

        this.returnWater = this.returnWater.add(report.getReturnWater());
    }

    public void increByMoneyOrderInfo(MoneyOrder moneyOrder)
    {
        MoneyOrderType moneyOrderType = MoneyOrderType.getType(moneyOrder.getType());
        if(moneyOrderType == MoneyOrderType.USER_RECHARGE)
        {
            this.recharge = this.recharge.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
        else if(moneyOrderType == MoneyOrderType.USER_WITHDRAW)
        {
            this.withdraw = this.withdraw.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
            this.feemoney = this.feemoney.add(moneyOrder.getFeemoney());
        }
        else if(moneyOrderType == MoneyOrderType.REFUND)
        {
            this.refund = this.refund.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
        else if(moneyOrderType == MoneyOrderType.PLATFORM_RECHARGE)
        {
            this.platformRecharge = this.platformRecharge.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
        else if(moneyOrderType == MoneyOrderType.PLATFORM_DEDUCT)
        {
            this.platformDeduct = this.platformDeduct.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
        else if(moneyOrderType == MoneyOrderType.PLATFORM_PRESENTATION)
        {
            this.platformPresentation = this.platformPresentation.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }

        // business
        else if(moneyOrderType == MoneyOrderType.BUSINESS_RECHARGE)
        {
            this.businessRecharge = this.businessRecharge.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
        else if(moneyOrderType == MoneyOrderType.BUSINESS_DEDUCT)
        {
            this.businessDeduct = this.businessDeduct.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount()));
            this.businessFeemoney = this.businessFeemoney.add(BigDecimalUtils.getNotNull(moneyOrder.getFeemoney()));
        }

        else if(moneyOrderType == MoneyOrderType.FINANCE_RECHARGE)
        {
            this.financeRecharge = this.financeRecharge.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
        else if(moneyOrderType == MoneyOrderType.FINANCE_DEDUCT)
        {
            this.financeDeduct = this.financeDeduct.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }

        else if(moneyOrderType == MoneyOrderType.RETURN_WATER)
        {
            this.returnWater = this.returnWater.add(BigDecimalUtils.getNotNull(moneyOrder.getAmount())) ;
        }
    }

    public void init()
    {
        this.recharge = BigDecimal.ZERO;
        this.withdraw = BigDecimal.ZERO;
        this.refund = BigDecimal.ZERO;
        this.feemoney = BigDecimal.ZERO;

        this.platformRecharge = BigDecimal.ZERO;
        this.platformDeduct = BigDecimal.ZERO;
        this.platformPresentation = BigDecimal.ZERO;

        this.businessRecharge = BigDecimal.ZERO;
        this.businessDeduct = BigDecimal.ZERO;
        this.businessFeemoney = BigDecimal.ZERO;

        // 理财
        this.financeRecharge = BigDecimal.ZERO;
        this.financeDeduct = BigDecimal.ZERO;
        this.financeFeemoney = BigDecimal.ZERO;

        this.totalProfit = BigDecimal.ZERO;

        this.returnWater = BigDecimal.ZERO;
    }


    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public void calcProfit()
    {
        this.totalProfit = this.totalProfit.add(recharge);
        this.totalProfit = this.totalProfit.subtract(withdraw);
        this.totalProfit = this.totalProfit.add(refund);
//
//        this.totalProfit = this.totalProfit.subtract(businessRecharge);
//        this.totalProfit = this.totalProfit.add(businessDeduct);
//
//        this.totalProfit = this.totalProfit.subtract(platformRecharge);
//        this.totalProfit = this.totalProfit.add(platformDeduct);
//        this.totalProfit = this.totalProfit.subtract(platformPresentation);
//
//        this.totalProfit = this.totalProfit.add(feemoney);
//
//        this.totalProfit = this.totalProfit.subtract(returnWater);

    }

    public BigDecimal getReturnWater() {
        return returnWater;
    }

    public void setReturnWater(BigDecimal returnWater) {
        this.returnWater = returnWater;
    }

    public BigDecimal getTotalBusinessProfitLoss() {
        return BigDecimalUtils.getNotNull(totalBusinessProfitLoss);
    }

    public void setTotalBusinessProfitLoss(BigDecimal totalBusinessProfitLoss) {
        this.totalBusinessProfitLoss = totalBusinessProfitLoss.setScale(2, RoundingMode.UP);
    }

    public BigDecimal getBusinessFeemoney() {
        return businessFeemoney;
    }

    public void setBusinessFeemoney(BigDecimal businessFeemoney) {
        this.businessFeemoney = businessFeemoney;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFinanceRecharge() {
        return financeRecharge;
    }

    public void setFinanceRecharge(BigDecimal financeRecharge) {
        this.financeRecharge = financeRecharge;
    }

    public BigDecimal getFinanceDeduct() {
        return financeDeduct;
    }

    public void setFinanceDeduct(BigDecimal financeDeduct) {
        this.financeDeduct = financeDeduct;
    }

    public BigDecimal getFinanceFeemoney() {
        return financeFeemoney;
    }

    public void setFinanceFeemoney(BigDecimal financeFeemoney) {
        this.financeFeemoney = financeFeemoney;
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

    public BigDecimal getLv1Recharge() {
        return lv1Recharge;
    }

    public void setLv1Recharge(BigDecimal lv1Recharge) {
        this.lv1Recharge = lv1Recharge;
    }

    public BigDecimal getLv1Withdraw() {
        return lv1Withdraw;
    }

    public void setLv1Withdraw(BigDecimal lv1Withdraw) {
        this.lv1Withdraw = lv1Withdraw;
    }

    public BigDecimal getLv2Recharge() {
        return lv2Recharge;
    }

    public void setLv2Recharge(BigDecimal lv2Recharge) {
        this.lv2Recharge = lv2Recharge;
    }

    public BigDecimal getLv2Withdraw() {
        return lv2Withdraw;
    }

    public void setLv2Withdraw(BigDecimal lv2Withdraw) {
        this.lv2Withdraw = lv2Withdraw;
    }
}
