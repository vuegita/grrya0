package com.inso.modules.passport.business.model;

import java.math.BigDecimal;

public class RechargePresentStatus {

    public static String KEY_PRESENT_AMOUNT = "presentAmount";

    private RechargePresentType type;
    private BigDecimal presentAmount;
    private BigDecimal limitMixAmount;
    private String remark;

    public RechargePresentType getType() {
        return type;
    }

    public void setType(RechargePresentType type) {
        this.type = type;
    }

    public BigDecimal getPresentAmount() {
        return presentAmount;
    }

    public void setPresentAmount(BigDecimal presentAmount) {
        this.presentAmount = presentAmount;
    }

    public BigDecimal getLimitMixAmount() {
        return limitMixAmount;
    }

    public void setLimitMixAmount(BigDecimal limitMixAmount) {
        this.limitMixAmount = limitMixAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
