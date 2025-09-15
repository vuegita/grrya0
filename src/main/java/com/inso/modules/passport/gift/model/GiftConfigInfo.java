package com.inso.modules.passport.gift.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class GiftConfigInfo {

    private long id;
    private String username;
    private String title;
    private String desc;
    private String targetType;
    private String periodType;
    private BigDecimal presentAmount;
    private BigDecimal limitAmount;

    /*** 当前金额 ***/
    private BigDecimal currentAmount;
    private long sort;

    private String status;

    private String presentArrEnable;
    private String presentArrValue;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public static String getColumnPrefix(){
        return "config";
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public BigDecimal getPresentAmount() {
        return presentAmount;
    }

    public void setPresentAmount(BigDecimal presentAmount) {
        this.presentAmount = presentAmount;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public long getSort() {
        return sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }

    public String getPresentArrEnable() {
        return presentArrEnable;
    }

    public void setPresentArrEnable(String presentArrEnable) {
        this.presentArrEnable = presentArrEnable;
    }

    public String getPresentArrValue() {
        return presentArrValue;
    }

    public void setPresentArrValue(String presentArrValue) {
        this.presentArrValue = presentArrValue;
    }
}
