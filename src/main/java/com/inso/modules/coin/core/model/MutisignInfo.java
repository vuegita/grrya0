package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class MutisignInfo {


    /**
     mutisign_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     mutisign_userid               int(11) NOT NULL,
     mutisign_username             varchar(255) NOT NULL ,

     mutisign_sender_address       varchar(255) NOT NULL,
     mutisign_currency_type        varchar(255) NOT NULL comment '所属代币',
     mutisign_balance              decimal(25,8) NOT NULL DEFAULT 0 comment '最新余额',

     mutisign_status               varchar(20) NOT NULL comment '授权状态',
     mutisign_createtime           datetime DEFAULT NULL ,
     mutisign_remark               varchar(1000) DEFAULT '',
     */

    private long id;

    private long userid;
    private String username;

    private long agentid;
    private String agentname;
    private String staffname;

    private String networkType;
    /*** 代币 ***/
    private String currencyType;
    /*** 授权者地址 ***/
    private String senderAddress;
    private BigDecimal balance;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;


    public static String getColumnPrefix(){
        return "mutisign";
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

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

}
