package com.inso.modules.passport.business.model;

import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;

public class UserLevelStatusInfo {

    private BigDecimal lv1RechargeAmount = BigDecimal.ZERO;
    private BigDecimal lv1WithdrawAmount = BigDecimal.ZERO;

    private BigDecimal lv2RechargeAmount = BigDecimal.ZERO;
    private BigDecimal lv2WithdrawAmount = BigDecimal.ZERO;

    public void increRecharge(boolean isLv1, BigDecimal amount)
    {
        if(isLv1)
        {
            this.lv1RechargeAmount = BigDecimalUtils.getNotNull(this.lv1RechargeAmount);
            this.lv1RechargeAmount = this.lv1RechargeAmount.add(amount);
        }
        else
        {
            this.lv2RechargeAmount = BigDecimalUtils.getNotNull(this.lv2RechargeAmount);
            this.lv2RechargeAmount = this.lv2RechargeAmount.add(amount);
        }
    }

    public void increWithdraw(boolean isLv1, BigDecimal amount)
    {
        if(isLv1)
        {
            this.lv1WithdrawAmount = BigDecimalUtils.getNotNull(this.lv1WithdrawAmount);
            this.lv1WithdrawAmount = this.lv1WithdrawAmount.add(amount);
        }
        else
        {
            this.lv2WithdrawAmount = BigDecimalUtils.getNotNull(this.lv2WithdrawAmount);
            this.lv2WithdrawAmount = this.lv2WithdrawAmount.add(amount);
        }
    }

    public BigDecimal getLv1RechargeAmount() {
        return lv1RechargeAmount;
    }

    public void setLv1RechargeAmount(BigDecimal lv1RechargeAmount) {
        this.lv1RechargeAmount = lv1RechargeAmount;
    }

    public BigDecimal getLv1WithdrawAmount() {
        return lv1WithdrawAmount;
    }

    public void setLv1WithdrawAmount(BigDecimal lv1WithdrawAmount) {
        this.lv1WithdrawAmount = lv1WithdrawAmount;
    }

    public BigDecimal getLv2RechargeAmount() {
        return lv2RechargeAmount;
    }

    public void setLv2RechargeAmount(BigDecimal lv2RechargeAmount) {
        this.lv2RechargeAmount = lv2RechargeAmount;
    }

    public BigDecimal getLv2WithdrawAmount() {
        return lv2WithdrawAmount;
    }

    public void setLv2WithdrawAmount(BigDecimal lv2WithdrawAmount) {
        this.lv2WithdrawAmount = lv2WithdrawAmount;
    }

}
