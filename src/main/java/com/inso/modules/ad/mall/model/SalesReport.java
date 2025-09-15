package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;

import java.math.BigDecimal;
import java.util.Date;

public class SalesReport {

    /**
     day_pdate                 date NOT NULL ,

     day_userid                int(11) NOT NULL DEFAULT 0,
     day_username              varchar(255) NOT NULL comment '商家',
     day_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_agentname             varchar(50) NOT NULL comment '所属代理',
     day_staffid               int(11) NOT NULL DEFAULT 0,
     day_staffname             varchar(50) NOT NULL,

     day_total_amount          decimal(18,2) NOT NULL DEFAULT 0 comment '销售总金额',
     day_total_count           int(11) NOT NULL DEFAULT 0 comment '销售总订单数',

     day_refund_amount         decimal(18,2) NOT NULL DEFAULT 0 comment '退款总额',
     day_refund_count          int(11) NOT NULL DEFAULT 0 comment '退款订单个数',

     day_remark                varchar(1000) NOT NULL DEFAULT '' comment  '',
     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long userid;
    private String username;
    private long staffid;
    private String staffname;
    private long agentid;
    private String agentname;


    private BigDecimal totalAmount;
    private long totalCount;
    private BigDecimal returnAmount;
    private BigDecimal refundAmount;
    private long refundCount;



    public static String getColumnPrefix(){
        return "day";
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
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


    public void incre(OrderTxStatus txStatus, AdEventOrderInfo orderInfo)
    {
        if(txStatus == OrderTxStatus.REALIZED)
        {
            if(orderInfo.getAmount() != null && orderInfo.getAmount().compareTo(BigDecimal.ZERO) > 0)
            {
                this.totalAmount = this.totalAmount.add(orderInfo.getAmount());
                this.totalCount++;

                this.returnAmount = this.returnAmount.add(orderInfo.getBrokerage());
            }
        }
        else
        {
            this.refundAmount = this.refundAmount.add(orderInfo.getAmount());
            this.refundCount++;
        }

    }


    public void init()
    {
        this.totalAmount = BigDecimal.ZERO;
        this.returnAmount = BigDecimal.ZERO;
        this.refundAmount = BigDecimal.ZERO;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public long getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(long refundCount) {
        this.refundCount = refundCount;
    }

    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }
}
