package com.inso.modules.game.fm.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class FMOrderInfo {

    /**
     *   order_no                    	varchar(50) NOT NULL comment '内部系统-订单号',
     *   order_fmid          	        int(11) NOT NULL comment '理解产品id',
     *
     *   order_fm_type               	varchar(20) NOT NULL comment '产品类型',
     *
     *   order_userid	                int(11) NOT NULL,
     *   order_username    			varchar(50) NOT NULL comment  '',
     *   order_agentid 	            int(11) NOT NULL comment '所属代理id',
     *
     *   order_status               	varchar(20) NOT NULL  comment '',
     *
     *   order_buy_amount              decimal(18,2) NOT NULL comment '认购金额',
     *   order_return_expected_amount  decimal(18,2) NOT NULL comment '预期收益金额',
     *   order_return_real_amount      decimal(18,2) NOT NULL DEFAULT 0 comment '实际收益金额',
     *
     *   order_feemoney                decimal(18,2) NOT NULL DEFAULT 0 comment '手续费',
     *
     *   order_createtime       		datetime NOT NULL,
     *   order_updatetime      		datetime DEFAULT NULL,
     *   order_remark             		varchar(1000) DEFAULT '',
     */

    private String no;
    private long fmid;
    private String fmType;
    private long userid;
    private String username;

    private String status;

    private BigDecimal returnRealRate;
    private BigDecimal buyAmount;
    private BigDecimal returnExpectedAmount;
    private BigDecimal returnRealAmount;
    /*** 利息支出 ***/
    private BigDecimal returnRealInterest;

    private BigDecimal feemoney;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;

    /***所属员工***/
    private String staffname;

    public static String getColumnPrefix(){
        return "order";
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public long getFmid() {
        return fmid;
    }

    public void setFmid(long fmid) {
        this.fmid = fmid;
    }

    public String getFmType() {
        return fmType;
    }

    public void setFmType(String fmType) {
        this.fmType = fmType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigDecimal getReturnExpectedAmount() {
        return returnExpectedAmount;
    }

    public void setReturnExpectedAmount(BigDecimal returnExpectedAmount) {
        this.returnExpectedAmount = returnExpectedAmount;
    }

    public BigDecimal getReturnRealAmount() {
        return returnRealAmount;
    }

    public void setReturnRealAmount(BigDecimal returnRealAmount) {
        this.returnRealAmount = returnRealAmount;
    }

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }


    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public BigDecimal getReturnRealInterest() {
        return returnRealInterest;
    }

    public void setReturnRealInterest(BigDecimal returnRealInterest) {
        this.returnRealInterest = returnRealInterest;
    }

    public BigDecimal getReturnRealRate() {
        return returnRealRate;
    }

    public void setReturnRealRate(BigDecimal returnRealRate) {
        this.returnRealRate = returnRealRate;
    }


    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }
}
