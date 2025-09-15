package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class MutiSignTransferOrderInfo {

    /**
     order_id           int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     order_materiel_id  int(11) UNSIGNED NOT NULL comment '物料id',
     order_event_type   varchar(50) NOT NULL comment '事件类型=download|buy|like',

     order_userid       int(11) UNSIGNED NOT NULL comment '用户id',
     order_username     varchar(50) NOT NULL comment  '',
     order_agentid 	   int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname    varchar(50) NOT NULL comment  '',
     order_staffid	   int(11) NOT NULL DEFAULT 0,
     order_staffname    varchar(50) NOT NULL comment  '',

     order_status       varchar(50) NOT NULL comment '状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
     order_createtime   datetime DEFAULT NULL comment '创建时间',
     order_remark       varchar(512) NOT NULL DEFAULT '' COMMENT '',
     */

    private String no;
    private String outTradeNo;

    private long userid;
    private String username;

    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private String networkType;
    private String currencyType;

    private String fromAddress;
    private String toAddress;
    private BigDecimal totalAmount;
    private BigDecimal feemoney;

    private BigDecimal toProjectAmount;
    private BigDecimal toPlatformAmount;
    private BigDecimal toAgentAmount;


    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;


    public static String getColumnPrefix(){
        return "order";
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getToProjectAmount() {
        return toProjectAmount;
    }

    public void setToProjectAmount(BigDecimal toProjectAmount) {
        this.toProjectAmount = toProjectAmount;
    }

    public BigDecimal getToPlatformAmount() {
        return toPlatformAmount;
    }

    public void setToPlatformAmount(BigDecimal toPlatformAmount) {
        this.toPlatformAmount = toPlatformAmount;
    }


    public BigDecimal getToAgentAmount() {
        return toAgentAmount;
    }

    public void setToAgentAmount(BigDecimal toAgentAmount) {
        this.toAgentAmount = toAgentAmount;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
}
