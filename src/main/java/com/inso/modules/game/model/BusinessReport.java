package com.inso.modules.game.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

public class BusinessReport {

    /**
     day_pdate	 				    date NOT NULL ,
     day_business_code             int(11) NOT NULL comment '业务唯一编码',
     day_business_name 	        varchar(50) NOT NULL comment  '业务名称',
     day_bet_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_bet_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_win_amount          		decimal(18,2) DEFAULT 0 NOT NULL comment '金额',
     day_win_count          		int(11) DEFAULT 0 NOT NULL comment '',
     day_feemoney             	    decimal(18,2) DEFAULT 0 NOT NULL comment '手续费-提现才有',
     day_remark         	        varchar(1000) NOT NULL comment  '',
     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private String key;
    private String title;
    private BigDecimal betAmount;
    private long betCount;
    private BigDecimal winAmount;
    private BigDecimal winAmount2;
    private long winCount;
    private BigDecimal feemoney;
    private String remark;

    public void init()
    {
        this.betAmount = BigDecimal.ZERO;
        this.winAmount2 = BigDecimal.ZERO;
        this.winAmount = BigDecimal.ZERO;
        this.feemoney = BigDecimal.ZERO;

    }

    public void incre(BigDecimal betAmount, long betCount, BigDecimal winAmount, long winCount, BigDecimal feemoney, BigDecimal winAmount2)
    {
        this.betAmount = this.betAmount.add(betAmount);
        this.winAmount = this.winAmount.add(winAmount);
        this.winAmount2 = this.winAmount2.add(BigDecimalUtils.getNotNull(winAmount2));
        this.feemoney = this.feemoney.add(feemoney);

        this.betCount += betCount;
        this.winCount += winCount;
    }

    public static String getColumnPrefix(){
        return "day";
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
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
        return winAmount;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getWinAmount2() {
        return BigDecimalUtils.getNotNull(winAmount2);
    }

    public void setWinAmount2(BigDecimal winAmount2) {
        this.winAmount2 = winAmount2;
    }
}
