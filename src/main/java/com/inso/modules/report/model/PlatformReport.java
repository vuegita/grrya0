package com.inso.modules.report.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;

public class PlatformReport {

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

    /*** 盈亏总额 ***/
    private BigDecimal totalProfit;

    public static String getColumnPrefix(){
        return "day";
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
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


    public void incre(MoneyOrder orderInfo)
    {
        MoneyOrderType moneyOrderType = MoneyOrderType.getType(orderInfo.getType());

        // 会员
        if(moneyOrderType == MoneyOrderType.USER_RECHARGE)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(recharge);
            this.recharge = totalAmount.add(orderInfo.getAmount());
        }
        else if(moneyOrderType == MoneyOrderType.USER_WITHDRAW)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(withdraw);
            this.withdraw = totalAmount.add(orderInfo.getAmount());

            BigDecimal totalFeemoney = BigDecimalUtils.getNotNull(feemoney);
            this.feemoney = totalFeemoney.add(orderInfo.getFeemoney());
        }
        else if(moneyOrderType == MoneyOrderType.REFUND)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(refund);
            this.refund = totalAmount.add(orderInfo.getAmount());
        }
        // 平台
        else if(moneyOrderType == MoneyOrderType.PLATFORM_RECHARGE)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(platformRecharge);
            this.platformRecharge = totalAmount.add(orderInfo.getAmount());
        }
        else if(moneyOrderType == MoneyOrderType.PLATFORM_DEDUCT)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(platformDeduct);
            this.platformDeduct = totalAmount.add(orderInfo.getAmount());
        }
        else if(moneyOrderType == MoneyOrderType.PLATFORM_PRESENTATION)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(platformPresentation);
            this.platformPresentation = totalAmount.add(orderInfo.getAmount());
        }
        // 业务
        else if(moneyOrderType == MoneyOrderType.BUSINESS_RECHARGE)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(businessRecharge);
            this.businessRecharge = totalAmount.add(orderInfo.getAmount());
        }
        else if(moneyOrderType == MoneyOrderType.BUSINESS_DEDUCT)
        {
            BigDecimal totalAmount = BigDecimalUtils.getNotNull(businessDeduct);
            this.businessDeduct = totalAmount.add(orderInfo.getAmount());

            BigDecimal totalFeemoney = BigDecimalUtils.getNotNull(businessFeemoney);
            this.businessFeemoney = totalFeemoney.add(orderInfo.getFeemoney());
        }
    }

    public void incre(PlatformReport report)
    {
        this.recharge = this.recharge.add(report.getRecharge());
        this.withdraw = this.withdraw.add(report.getWithdraw());
        this.feemoney = this.feemoney.add(report.getFeemoney());

        this.platformPresentation = this.platformPresentation.add(report.getPlatformPresentation());
        this.platformRecharge = this.platformRecharge.add(report.getPlatformRecharge());
        this.platformDeduct = this.platformDeduct.add(report.getPlatformDeduct());

        this.businessDeduct = this.businessDeduct.add(report.getBusinessDeduct());
        if(this.businessFeemoney == null) {
            this.businessFeemoney = BigDecimal.ZERO;
        }
        this.businessFeemoney = this.businessFeemoney.add(BigDecimalUtils.getNotNull(report.getBusinessFeemoney()));

        this.businessRecharge = this.businessRecharge.add(report.getBusinessRecharge());
        this.refund = this.refund.add(report.getRefund());

        this.returnWater = this.returnWater.add(report.getReturnWater());
    }

    /**
     * 今日统计报表,合计到平台报表，就是平台汇总
     * @param report
     */
    public void incre(MemberReport report)
    {
        this.recharge = this.recharge.add(report.getRecharge());
        this.withdraw = this.withdraw.add(report.getWithdraw());
        this.feemoney = this.feemoney.add(report.getFeemoney());

        this.platformPresentation = this.platformPresentation.add(report.getPlatformPresentation());
        this.platformRecharge = this.platformRecharge.add(report.getPlatformRecharge());
        this.platformDeduct = this.platformDeduct.add(report.getPlatformDeduct());

        this.businessDeduct = this.businessDeduct.add(report.getBusinessDeduct());
        this.businessRecharge = this.businessRecharge.add(report.getBusinessRecharge());
        this.businessFeemoney=this.businessFeemoney.add(report.getBusinessFeemoney());
        this.refund = this.refund.add(report.getRefund());

        this.returnWater = this.returnWater.add(report.getReturnWater());
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

        this.returnWater = BigDecimal.ZERO;

        this.totalProfit = BigDecimal.ZERO;
    }

    public void calcProfit()
    {
        this.totalProfit = this.totalProfit.add(recharge);
        this.totalProfit = this.totalProfit.subtract(withdraw);
        this.totalProfit = this.totalProfit.add(refund);

//        this.totalProfit = this.totalProfit.subtract(businessRecharge);
//        this.totalProfit = this.totalProfit.add(businessDeduct);
//
//        this.totalProfit = this.totalProfit.subtract(platformRecharge);
//        this.totalProfit = this.totalProfit.add(platformDeduct);
//        this.totalProfit = this.totalProfit.subtract(platformPresentation);
//
//        this.totalProfit = this.totalProfit.add(feemoney);
//        this.totalProfit = this.totalProfit.subtract(returnWater);
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getReturnWater() {
        return returnWater;
    }

    public void setReturnWater(BigDecimal returnWater) {
        this.returnWater = returnWater;
    }

    public BigDecimal getBusinessFeemoney() {
        return businessFeemoney;
    }

    public void setBusinessFeemoney(BigDecimal businessFeemoney) {
        this.businessFeemoney = businessFeemoney;
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
}
