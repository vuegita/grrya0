package com.inso.modules.web.settle.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class SettleRecordInfo {

    /**
     *   record_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   record_pdate                 date NOT NULL ,
     *   record_business_type         varchar(20) NOT NULL comment '业务类型',
     *
     *   record_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     *   record_agentname             varchar(255) NOT NULL comment  '',
     *   record_staffid               int(11) NOT NULL DEFAULT 0,
     *   record_staffname             varchar(255) NOT NULL comment  '',
     *
     *   record_currency              varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',
     *
     *   record_amount                decimal(25,8) NOT NULL comment '流水金额',
     *   record_feemoney              decimal(25,8) NOT NULL comment '手续费',
     *   record_remark                varchar(3000) NOT NULL DEFAULT '' comment '',
     */

    private long id;
    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;

    private String businessType;

    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private String currency;

    private BigDecimal amount;
    private BigDecimal feemoney;
    private String remark;

    public static String getColumnPrefix(){
        return "record";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }
}
