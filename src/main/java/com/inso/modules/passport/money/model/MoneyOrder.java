package com.inso.modules.passport.money.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class MoneyOrder {

    /**
     *  um_order_no                    	varchar(30) NOT NULL comment '内部系统-订单号' ,
     *   um_order_out_trade_no    			varchar(30) NOT NULL comment  '内部系统-订单号, 业务订单号',
     *   um_order_userid	                int(11) NOT NULL,
     *   um_order_username    			    varchar(50) NOT NULL comment  '',
     *   um_order_business_type		    varchar(50) NOT NULL comment  '业务类型',
     *   um_order_type         			varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduction=系统扣款|refund=退款' ,
     *   um_order_status               	varchar(20) NOT NULL  comment 'new=待支付 | realized=处理成功 | error=失败',
     *   um_order_balance            		decimal(18,2) NOT NULL DEFAULT 0 comment '余额',
     *   um_order_amount             		decimal(18,2) NOT NULL comment '流水金额',
     *   um_order_feemoney					decimal(18,2) NOT NULL comment '手续费-提现才有',
     *   um_order_createtime       		datetime NOT NULL comment '和报表时间要一样',
     *   um_order_updatetime      			datetime DEFAULT NULL,
     *   um_order_remark             		varchar(1000) DEFAULT '',
     */

    private String no;
    private String outTradeNo;
    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private String businessType;
    private String type;
    private String status;
    private BigDecimal balance;
    private BigDecimal amount;
    private BigDecimal feemoney;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatime;
    /*** 业务信息备注-showRemark为保留字段，显示时使用{msg:"xxx"} ***/
    private String remark;

    private String fundKey;
    private String currency;

    public static String getColumnPrefix(){
        return "um_order";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
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

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public Date getUpdatime() {
        return updatime;
    }

    public void setUpdatime(Date updatime) {
        this.updatime = updatime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFundKey() {
        return fundKey;
    }

    public void setFundKey(String fundKey) {
        this.fundKey = fundKey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
}
