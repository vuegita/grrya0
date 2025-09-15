package com.inso.modules.coin.qrcode.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class QrcodeConfigInfo {

    /**
     *   config_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *
     *   config_userid                int(11) UNSIGNED NOT NULL comment '员工ID',
     *   config_username              varchar(255) NOT NULL comment  '员工用户名',
     *   config_agentid               int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     *   config_agentname             varchar(255) NOT NULL comment  '',
     *
     *   config_name                  varchar(255) NOT NULL DEFAULT '' comment '产品名称',
     *   config_contractid            int(11) NOT NULL comment '合约id',
     *   config_network_type          varchar(255) NOT NULL comment '网络类型',
     *   config_currency              varchar(255) NOT NULL comment '所属币种',
     *
     *   config_amount                decimal(25,8) NOT NULL DEFAULT 0 comment '最小提现金额',
     *
     *   config_status                varchar(20) NOT NULL comment '状态',
     *   config_createtime            datetime DEFAULT NULL ,
     *   config_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;
    private long userid;
    private String username;
    private long agentid;
    private String agentname;

    private String name;

    private long contractid;
    private String networkType;
    private String currency;

    private String type;
    private String address;
    private BigDecimal amount;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "config";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
