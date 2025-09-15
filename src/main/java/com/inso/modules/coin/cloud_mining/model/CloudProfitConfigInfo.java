package com.inso.modules.coin.cloud_mining.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class CloudProfitConfigInfo {

    /**
     config_currency               varchar(255) NOT NULL comment '币种',
     config_level                  int(11) UNSIGNED NOT NULL ,

     config_amount                 decimal(25,8) NOT NULL DEFAULT 0 comment '投资最低金额',
     config_daily_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '收益率',

     config_status                 varchar(20) NOT NULL comment '状态',
     config_createtime             datetime NOT NULL comment '创建时间',
     config_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    private long days;
    private long level;

    private BigDecimal minAmount;
    private BigDecimal dailyRate;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "config";
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
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

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}
