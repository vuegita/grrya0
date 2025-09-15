package com.inso.modules.passport.business.model;

import java.math.BigDecimal;

public class ReturnWaterLogDetail {

    private String username;
    private String childname;
    private BigDecimal amount;
    private String currency;

    public static String getColumnPrefix(){
        return "detail";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChildname() {
        return childname;
    }

    public void setChildname(String childname) {
        this.childname = childname;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
