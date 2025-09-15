package com.inso.modules.web.team.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class TeamOrderInfo {

    /**
     order_no                    	varchar(255) NOT NULL comment '内部系统-订单号',
     order_out_trade_no       	    varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有hash',

     order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
     order_username                varchar(255) NOT NULL comment  '',
     order_agentid 	            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
     order_agentname               varchar(255) NOT NULL comment  '',
     order_staffid	                int(11) NOT NULL DEFAULT 0,
     order_staffname               varchar(255) NOT NULL comment  '',

     order_network_type	        varchar(255) NOT NULL comment '所属网络',
     order_currency_type	        varchar(255) NOT NULL comment '所属代币',
     order_amount                  decimal(25,8) NOT NULL comment '金额',
     order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

     order_type         			varchar(50) NOT NULL DEFAULT '' comment  '订单类型: 结算收益|提现到余额',
     order_status               	varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
     order_createtime       		datetime NOT NULL,
     order_updatetime      		datetime DEFAULT NULL,
     order_remark             		varchar(1000) DEFAULT '',
     */

    private String no;
    private String outTradeNo;
    private String businessType;

    private long groupId;
    private long recordId;

    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private String currencyType;

    private BigDecimal amount;
    private BigDecimal feemoney;

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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }


    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
