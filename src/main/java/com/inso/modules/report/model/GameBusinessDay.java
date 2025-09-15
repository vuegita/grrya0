package com.inso.modules.report.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.common.model.OrderTxStatus;

public class GameBusinessDay {

    /**
     day_pdate	 				    date NOT NULL ,
     day_agentid 	                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_agentname 	            varchar(50) NOT NULL comment '所属代理',
     day_staffid	                int(11) NOT NULL DEFAULT 0,
     day_staffname 	            varchar(50) NOT NULL,

     day_business_code             int(11) NOT NULL comment '业务唯一编码',
     day_business_name           varchar(50) NOT NULL comment  '业务名称',

     day_bet_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_bet_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_win_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_win_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_feemoney             	    decimal(18,2) DEFAULT 0 NOT NULL comment '业务手续费',

     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private long businessCode;
    private String businessName;

    private BigDecimal betAmount;
    private long betCount;

    private BigDecimal winAmount;
    private long winCount;
    private BigDecimal feemoney;

    private String status;

    private BigDecimal totalBetAmount = BigDecimal.ZERO;

    private long totalRecordCount;

    public static String getColumnPrefix(){
        return "order";
    }

    public void init()
    {
        this.betAmount = BigDecimal.ZERO;
        this.winAmount = BigDecimal.ZERO;
        this.feemoney = BigDecimal.ZERO;
    }

    public void incre(GameBusinessDay businessDay)
    {
        if(businessDay.getWinAmount() != null && businessDay.getWinAmount().compareTo(BigDecimal.ZERO) > 0)
        {
            this.winAmount = this.getWinAmount().add(businessDay.getWinAmount());
            this.winCount += businessDay.getWinCount();
        }

        if(businessDay.getBetAmount() != null)
        {
            this.betAmount = this.getBetAmount().add(businessDay.getBetAmount());
            this.betCount +=  businessDay.getBetCount();
        }

        if(businessDay.getFeemoney() != null && businessDay.getFeemoney().compareTo(BigDecimal.ZERO) > 0)
        {
            this.feemoney = this.getFeemoney().add(businessDay.getFeemoney());
        }
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

    public BigDecimal getBetAmount() {
        return BigDecimalUtils.getNotNull(betAmount);
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public long getBetCount() {
        return betCount;
    }

    public void setBetCount(long betCount) {
        this.betCount = betCount;
    }

    public BigDecimal getWinAmount() {
        return BigDecimalUtils.getNotNull(winAmount);
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    public long getWinCount() {
        return winCount;
    }

    public void setWinCount(long winCount) {
        this.winCount = winCount;
    }


    public long getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(long businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }



    public BigDecimal getFeemoney() {
        return BigDecimalUtils.getNotNull(feemoney);
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public long getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }
}
