package com.inso.modules.report.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.common.model.OrderTxStatus;

import java.math.BigDecimal;
import java.util.Date;

public class BusinessV2Report {

    /**
     day_pdate	 				date NOT NULL ,
     day_agentid          		int(11) NOT NULL comment 'userid',
     day_agentname    			varchar(100) NOT NULL comment  '',
     day_staffid	            int(11) NOT NULL DEFAULT 0,
     day_staffname    			varchar(100) NOT NULL comment  '',

     day_business_key	        varchar(255) NOT NULL comment '所属业务key',
     day_business_name	        varchar(255) NOT NULL comment '所属业务名称',
     day_business_externalid   varchar(255) NOT NULL comment '业务拓展ID',

     day_recharge           	decimal(25,8) NOT NULL DEFAULT 0 comment '业务充值',
     day_deduct            	decimal(25,8) NOT NULL DEFAULT 0 comment '业务扣款',
     day_feemoney              decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',
     day_return_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',

     day_remark             	varchar(1000) NOT NULL DEFAULT '' comment '',
     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private String businessKey;
    private String businessName;
    private String businessExternalid;

    private String dimensionType;
    private String currencyType;

    private BigDecimal rechargeAmount = BigDecimal.ZERO;
    private long totalRechargeCount = 0;
    private long successRechargeCount;

    private BigDecimal deductAmount = BigDecimal.ZERO;
    private long totalDeductCount;
    private long successDeductCount;

    private BigDecimal feemoney = BigDecimal.ZERO;
    private String remark;

    public static String getColumnPrefix(){
        return "day";
    }


    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
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

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessExternalid() {
        return businessExternalid;
    }

    public void setBusinessExternalid(String businessExternalid) {
        this.businessExternalid = businessExternalid;
    }

    public BigDecimal getRechargeAmount() {
        return BigDecimalUtils.getNotNull(rechargeAmount);
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public BigDecimal getDeductAmount() {
        return BigDecimalUtils.getNotNull(deductAmount);
    }

    public void setDeductAmount(BigDecimal deductAmount) {
        this.deductAmount = deductAmount;
    }

    public void increRechargeAmount(BigDecimal valueAmount, long recordCount)
    {
        if(valueAmount != null && valueAmount.compareTo(BigDecimal.ZERO) > 0)
        {
//            this.totalRechargeCount += recordCount;
            this.rechargeAmount = this.rechargeAmount.add(valueAmount);
        }
    }

    public void increDeductAmount(BigDecimal valueAmount, OrderTxStatus txStatus, long recordCount, boolean forceAdd)
    {
        if(valueAmount == null || valueAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }

        boolean isAdd = false;
        if(forceAdd)
        {
            this.deductAmount = this.deductAmount.add(valueAmount);
            isAdd = true;
        }
        this.totalDeductCount += recordCount;
        if(txStatus == OrderTxStatus.REALIZED)
        {
            if(!isAdd)
            {
                this.deductAmount = this.deductAmount.add(valueAmount);
            }
            this.successDeductCount += recordCount;
        }
    }

    public void increFeemoney(BigDecimal valueAmount)
    {
        if(valueAmount != null && valueAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            this.feemoney = this.feemoney.add(valueAmount);
        }
    }

    public String getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
    }

    public long getTotalRechargeCount() {
        return totalRechargeCount;
    }

    public void setTotalRechargeCount(long totalRechargeCount) {
        this.totalRechargeCount = totalRechargeCount;
    }

    public long getSuccessRechargeCount() {
        return successRechargeCount;
    }

    public void setSuccessRechargeCount(long successRechargeCount) {
        this.successRechargeCount = successRechargeCount;
    }

    public long getTotalDeductCount() {
        return totalDeductCount;
    }

    public void setTotalDeductCount(long totalDeductCount) {
        this.totalDeductCount = totalDeductCount;
    }

    public long getSuccessDeductCount() {
        return successDeductCount;
    }

    public void setSuccessDeductCount(long successDeductCount) {
        this.successDeductCount = successDeductCount;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }
}
