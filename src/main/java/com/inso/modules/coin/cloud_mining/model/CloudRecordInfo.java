package com.inso.modules.coin.cloud_mining.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class CloudRecordInfo {

    /**
     record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     record_product_id             int(11) NOT NULL ,
     record_account_id             int(11) NOT NULL ,

     record_userid                 int(11) NOT NULL ,
     record_username 	            varchar(255) NOT NULL comment '用户名',
     record_address 	            varchar(255) NOT NULL comment '用户地址',

     record_reward_amount          decimal(25,8) NOT NULL DEFAULT 0 comment '收益金额',

     record_status                varchar(20) NOT NULL comment '状态',
     record_createtime  	        datetime DEFAULT NULL ,
     record_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    private long userid;
    private String username;
    private String address;

    private String currencyType;
    private String productType;

    private BigDecimal moneyBalance;
    private BigDecimal coldAmount;

    private long days;
    /*** 自动提现到余额 ***/
    private BigDecimal invesTotalAmount;
    private BigDecimal rewardBalance;
    private BigDecimal totalRewardAmount;


    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;
    private String remark;

    public static String getColumnPrefix(){
        return "record";
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getTotalRewardAmount() {
        return totalRewardAmount;
    }

    public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
        this.totalRewardAmount = totalRewardAmount;
    }

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public BigDecimal getColdAmount() {
        return coldAmount;
    }

    public void setColdAmount(BigDecimal coldAmount) {
        this.coldAmount = coldAmount;
    }

    public BigDecimal getInvesTotalAmount() {
        return invesTotalAmount;
    }

    public void setInvesTotalAmount(BigDecimal invesTotalAmount) {
        this.invesTotalAmount = invesTotalAmount;
    }

    public BigDecimal getRewardBalance() {
        return rewardBalance;
    }

    public void setRewardBalance(BigDecimal rewardBalance) {
        this.rewardBalance = rewardBalance;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}
