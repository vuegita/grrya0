package com.inso.modules.game.red_package.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class RedPStaffLimit {

    /**
     *   config_agentid       		    	int(11)  NOT NULL ,
     *   config_agentname    			    varchar(50) NOT NULL ,
     *   config_staffid       			    int(11)  NOT NULL ,
     *   config_staffname    		        varchar(50) NOT NULL ,
     *
     *   config_max_money_of_day           decimal(18,2) NOT NULL comment '每天最大金额',
     *   config_max_count_of_day           int(11) NOT NULL comment '每天发送金额次数',
     *
     *   config_createtime                 datetime NOT NULL,
     *   config_status                     varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     *   config_remark 			        varchar(500) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    /*** 单笔最大金额 ***/
    private BigDecimal maxMoneyOfSingle;
    /*** 每日最大金额 ***/
    private BigDecimal maxMoneyOfDay;
    private long maxCountOfDay;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String status;
    private String remark;

    public static String getColumnPrefix(){
        return "limit";
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

    public BigDecimal getMaxMoneyOfDay() {
        return maxMoneyOfDay;
    }

    public void setMaxMoneyOfDay(BigDecimal maxMoneyOfDay) {
        this.maxMoneyOfDay = maxMoneyOfDay;
    }

    public long getMaxCountOfDay() {
        return maxCountOfDay;
    }

    public void setMaxCountOfDay(long maxCountOfDay) {
        this.maxCountOfDay = maxCountOfDay;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getMaxMoneyOfSingle() {
        return maxMoneyOfSingle;
    }

    public void setMaxMoneyOfSingle(BigDecimal maxMoneyOfSingle) {
        this.maxMoneyOfSingle = maxMoneyOfSingle;
    }
}
