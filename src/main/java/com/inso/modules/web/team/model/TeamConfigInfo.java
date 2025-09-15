package com.inso.modules.web.team.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class TeamConfigInfo {

    /**
     config_id                           int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     config_agentid                      int(11) NOT NULL DEFAULT 0 comment '',
     config_agentname                    varchar(255) NOT NULL comment  '',

     config_business_type                varchar(255) NOT NULL comment  '',
     config_level                        int(11) NOT NULL DEFAULT 1 comment '当前团队等级',
     config_currency_type                varchar(255) NOT NULL comment  '',

     config_limit_min_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '需要最低投资多少金额',
     config_limit_min_invite_count       int(11) NOT NULL comment '需要邀请总人数',

     config_return_rate                  decimal(25,8) NOT NULL DEFAULT 0 comment '返回比例',

     config_status                       varchar(20) NOT NULL comment '状态',
     config_createtime                   datetime NOT NULL comment '创建时间',
     config_remark                       varchar(3000) NOT NULL DEFAULT '' comment '备注',

     */

    private long id;
    private String key;
    private long agentid;
    private String agentname;

    private String businessType;
    private String currencyType;
    private long level;

    private BigDecimal limitBalanceAmount;
    private BigDecimal limitMinAmount;
    private long limitMinInviteCount;
    private String returnCreatorRate;
    private BigDecimal returnJoinRate;

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

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public BigDecimal getLimitMinAmount() {
        return limitMinAmount;
    }

    public void setLimitMinAmount(BigDecimal limitMinAmount) {
        this.limitMinAmount = limitMinAmount;
    }

    public long getLimitMinInviteCount() {
        return limitMinInviteCount;
    }

    public void setLimitMinInviteCount(long limitMinInviteCount) {
        this.limitMinInviteCount = limitMinInviteCount;
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


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReturnCreatorRate() {
        return returnCreatorRate;
    }

    public void setReturnCreatorRate(String returnCreatorRate) {
        this.returnCreatorRate = returnCreatorRate;
    }

    public BigDecimal getReturnJoinRate() {
        return returnJoinRate;
    }

    public void setReturnJoinRate(BigDecimal returnJoinRate) {
        this.returnJoinRate = returnJoinRate;
    }

    public BigDecimal getLimitBalanceAmount() {
        return limitBalanceAmount;
    }

    public void setLimitBalanceAmount(BigDecimal limitBalanceAmount) {
        this.limitBalanceAmount = limitBalanceAmount;
    }
}
