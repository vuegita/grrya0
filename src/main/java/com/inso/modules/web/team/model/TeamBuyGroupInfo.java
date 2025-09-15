package com.inso.modules.web.team.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class TeamBuyGroupInfo {

    /*
         group_id                             int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     group_business_type                  varchar(255) NOT NULL comment  '',
     group_config_id                      int(11) NOT NULL DEFAULT 0 comment '',

     group_agentid                        int(11) NOT NULL comment '',
     group_agentname                      varchar(255) NOT NULL comment  '',
     group_staffid                        int(11) NOT NULL comment '',
     group_staffname                      varchar(255) NOT NULL comment  '',
     group_userid                         int(11) NOT NULL comment '',
     group_username                       varchar(255) NOT NULL comment  '',

     group_need_invite_count              int(11) NOT NULL comment '',
     group_has_invite_count               int(11) NOT NULL comment '',
     group_return_rate                    decimal(25,8) NOT NULL DEFAULT 0 comment '返回比例',

     group_status                         varchar(20) NOT NULL DEFAULT 'waiting' comment '完成状态',
     group_createtime                     datetime NOT NULL comment '创建时间',
     group_endtime                        datetime NOT NULL comment '结束时间',
     group_remark                         varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;
    private String key;

    private String businessType;
    private String currencyType;
    private long configId;
    private long recordId;

    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private BigDecimal needInvesAmount;
    private BigDecimal realInvesAmount;
    private long needInviteCount;
    private long hasInviteCount;
    private String returnCreatorRate;
    private BigDecimal returnJoinRate;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;
    private String remark;

    public static String getColumnPrefix(){
        return "group";
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

    public long getNeedInviteCount() {
        return needInviteCount;
    }

    public void setNeedInviteCount(long needInviteCount) {
        this.needInviteCount = needInviteCount;
    }

    public long getHasInviteCount() {
        return hasInviteCount;
    }

    public void setHasInviteCount(long hasInviteCount) {
        this.hasInviteCount = hasInviteCount;
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


    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public long getConfigId() {
        return configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }

    public BigDecimal getNeedInvesAmount() {
        return needInvesAmount;
    }

    public void setNeedInvesAmount(BigDecimal needInvesAmount) {
        this.needInvesAmount = needInvesAmount;
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

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public BigDecimal getRealInvesAmount() {
        return realInvesAmount;
    }

    public void setRealInvesAmount(BigDecimal realInvesAmount) {
        this.realInvesAmount = realInvesAmount;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
