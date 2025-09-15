package com.inso.modules.coin.binance_activity.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class BARecordInfo {

    /**
     record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     record_contractid            int(11) NOT NULL comment '合约id',
     record_network_type           varchar(255) NOT NULL comment  '',
     record_currency_type          varchar(255) NOT NULL comment '投资币种',

     record_userid                 int(11) UNSIGNED NOT NULL comment '用户id',
     record_username               varchar(255) NOT NULL comment  '',
     record_address                varchar(255) NOT NULL DEFAULT '' comment '用户地址',

     record_status                 varchar(20) NOT NULL comment '状态',
     record_createtime             datetime NOT NULL comment '创建时间',
     record_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    /*** 合约ID ***/
    private long contractid;
    private String networkType;
    private String currencyType;

    private long userid;
    private String username;
    private String address;

    private BigDecimal totalRewardAmount;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

//    private long agentid;
//    private String agentname;

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

    public long getContractid() {
        return contractid;
    }

    public void setContractid(long contractid) {
        this.contractid = contractid;
    }


    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public BigDecimal getTotalRewardAmount() {
        return totalRewardAmount;
    }

    public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
        this.totalRewardAmount = totalRewardAmount;
    }

}
