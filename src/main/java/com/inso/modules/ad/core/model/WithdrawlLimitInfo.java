package com.inso.modules.ad.core.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现额度
 */
public class WithdrawlLimitInfo {

    private long id;
    private long userid;
    private String username;

    private BigDecimal amount;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public static String getColumnPrefix(){
        return "withdrawl";
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean verifyWithdraw(BigDecimal withdrawAmount)
    {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return false;
        }

        return amount.compareTo(withdrawAmount) >= 0;
    }

}
