package com.inso.modules.web.activity.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class ActivityInfo {

    /*
     activity_id                               int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     activity_title                            varchar(255) NOT NULL DEFAULT '' comment  '',

     activity_agentid                          int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     activity_agentname                        varchar(255) NOT NULL DEFAULT '' comment  '',
     activity_staffid                          int(11) NOT NULL DEFAULT 0,
     activity_staffname                        varchar(255) NOT NULL DEFAULT '' comment  '',

     activity_business_type                    varchar(255) NOT NULL comment  '',
     activity_currency_type                    varchar(255) NOT NULL comment  '',

     activity_limit_min_invite_count           int(11) NOT NULL DEFAULT 0 comment '最低邀请人数',
     activity_limit_min_inves_amount        decimal(25,8) NOT NULL DEFAULT 0 comment '最低投资金额',

     activity_basic_present_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '邀请完成基础赠送',
     activity_extra_present_tier               decimal(25,8) NOT NULL DEFAULT 0 comment '通过比例额外赠送',

     activity_finish_invite_count              int(11) NOT NULL DEFAULT 0 comment '统计-总完成人数',
     activity_finish_inves_count
     activity_finish_inves_amount           decimal(25,8) NOT NULL DEFAULT 0 comment '统计-总完成充值金额',
     activity_finish_present_amount            decimal(25,8) NOT NULL DEFAULT 0 comment '统计-赠送总金额',

     activity_status                           varchar(20) NOT NULL comment 'waiting' comment '状态',
     activity_createtime                       datetime NOT NULL comment '创建时间',
     activity_begintime                        datetime NOT NULL comment '开始时间',
     activity_endtime                          datetime NOT NULL comment '结束时间',
     activity_remark                           varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;
    private String title;

    private String businessType;
    private String currencyType;

    private long limitMinInviteCount;
    private BigDecimal limitMinInvesAmount;
    private BigDecimal basicPresentAmount;
    private String extraPresentTier;

    private long finishInviteCount;
    private long finishInvesCount;
    private BigDecimal finishInvesAmount;
    private BigDecimal finishPresentAmount;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date begintime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;
    private String remark;

    public static String getColumnPrefix(){
        return "activity";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getLimitMinInviteCount() {
        return limitMinInviteCount;
    }

    public void setLimitMinInviteCount(long limitMinInviteCount) {
        this.limitMinInviteCount = limitMinInviteCount;
    }

    public BigDecimal getLimitMinInvesAmount() {
        return limitMinInvesAmount;
    }

    public void setLimitMinInvesAmount(BigDecimal limitMinInvesAmount) {
        this.limitMinInvesAmount = limitMinInvesAmount;
    }

    public BigDecimal getBasicPresentAmount() {
        return basicPresentAmount;
    }

    public void setBasicPresentAmount(BigDecimal basicPresentAmount) {
        this.basicPresentAmount = basicPresentAmount;
    }

    public long getFinishInviteCount() {
        return finishInviteCount;
    }

    public void setFinishInviteCount(long finishInviteCount) {
        this.finishInviteCount = finishInviteCount;
    }

    public BigDecimal getFinishInvesAmount() {
        return finishInvesAmount;
    }

    public void setFinishInvesAmount(BigDecimal finishInvesAmount) {
        this.finishInvesAmount = finishInvesAmount;
    }

    public BigDecimal getFinishPresentAmount() {
        return finishPresentAmount;
    }

    public void setFinishPresentAmount(BigDecimal finishPresentAmount) {
        this.finishPresentAmount = finishPresentAmount;
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

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getExtraPresentTier() {
        return extraPresentTier;
    }

    public void setExtraPresentTier(String extraPresentTier) {
        this.extraPresentTier = extraPresentTier;
    }

    public long getFinishInvesCount() {
        return finishInvesCount;
    }

    public void setFinishInvesCount(long finishInvesCount) {
        this.finishInvesCount = finishInvesCount;
    }
}
