package com.inso.modules.game.rg.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class LotteryPeriodInfo {

    private String issue;
    private String type;
    private long gameid;
    private BigDecimal totalBetAmount;
    private BigDecimal totalWinAmount;
    private BigDecimal totalFeemoney;
    private long totalBetCount;
    private long totalWinCount;
    private String status;
    private String openMode;
    private String referencePrice;
    private long openResult;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date starttime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;
    private Date createtime;
    private Date updatetime;
    private String remark;

    public static String getColumnPrefix(){
        return "period";
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getGameid() {
        return gameid;
    }

    public void setGameid(long gameid) {
        this.gameid = gameid;
    }

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public BigDecimal getTotalWinAmount() {
        return totalWinAmount;
    }

    public void setTotalWinAmount(BigDecimal totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    public BigDecimal getTotalFeemoney() {
        return totalFeemoney;
    }

    public void setTotalFeemoney(BigDecimal totalFeemoney) {
        this.totalFeemoney = totalFeemoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public long getOpenResult() {
        return openResult;
    }

    public void setOpenResult(long openResult) {
        this.openResult = openResult;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTotalBetCount() {
        return totalBetCount;
    }

    public void setTotalBetCount(long totalBetCount) {
        this.totalBetCount = totalBetCount;
    }

    public long getTotalWinCount() {
        return totalWinCount;
    }

    public void setTotalWinCount(long totalWinCount) {
        this.totalWinCount = totalWinCount;
    }

    public String getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(String referencePrice) {
        this.referencePrice = referencePrice;
    }


//    public static enum MyPeriodStatus {
//
//
//
//
//    }

}
