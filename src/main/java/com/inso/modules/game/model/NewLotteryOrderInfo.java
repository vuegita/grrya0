package com.inso.modules.game.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.Date;

public class NewLotteryOrderInfo {


    private String no;
    private String issue;
    private String lotteryType;
    private String betItem;
    private String openResult;
    private String referenceExt;
    private String referenceSeed1;

    private long agentid;
    private long staffid;
    private String agentname;
    private String staffname;
    private long userid;
    private String username;
    private String usertype;

    private BigDecimal singleBetAmount;
    private BigDecimal totalBetAmount;
    private long totalBetCount;
    private BigDecimal winAmount;
    private BigDecimal feemoney;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    private String remark;


    /*** 总充值金额 ***/
    private BigDecimal totalRecharge;
    /*** 总提现金额 ***/
    private BigDecimal totalWithdraw;
    /***余额 ***/
    private BigDecimal balance;

    /*** 统计数据 ***/
    private long totalRecordCount;

    private transient int tmpCalcWinCount;
    private transient BigDecimal tmpWinAmount2;

    private Object betItemResultList;

    public static String getColumnPrefix(){
        return "order";
    }

    public String getStaffname() {
        return staffname;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(BigDecimal totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public BigDecimal getTotalWithdraw() {
        return totalWithdraw;
    }

    public void setTotalWithdraw(BigDecimal totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public long getStaffid() {
        return staffid;
    }

    public void setStaffid(long staffid) {
        this.staffid = staffid;
    }



    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getBetItem() {
        return betItem;
    }

    public void setBetItem(String betItem) {
        this.betItem = betItem;
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
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

    public BigDecimal getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    public BigDecimal getFeemoney() {
        return feemoney;
    }

    public void setFeemoney(BigDecimal feemoney) {
        this.feemoney = feemoney;
    }

    public String getLotteryType() {
        return lotteryType;
    }

    public void setLotteryType(String lotteryType) {
        this.lotteryType = lotteryType;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public BigDecimal getSingleBetAmount() {
        return singleBetAmount;
    }

    public void setSingleBetAmount(BigDecimal singleBetAmount) {
        this.singleBetAmount = singleBetAmount;
    }

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public long getTotalBetCount() {
        return totalBetCount;
    }

    public void setTotalBetCount(long totalBetCount) {
        this.totalBetCount = totalBetCount;
    }

    public int getTmpCalcWinCount() {
        return tmpCalcWinCount;
    }

    public void setTmpCalcWinCount(int tmpCalcWinCount) {
        this.tmpCalcWinCount = tmpCalcWinCount;
    }

    public Object getBetItemResultList() {
        return betItemResultList;
    }

    public void setBetItemResultList(Object betItemResultList) {
        this.betItemResultList = betItemResultList;
    }

    public String getReferenceExt() {
        return referenceExt;
    }

    public void setReferenceExt(String referenceExt) {
        this.referenceExt = referenceExt;
    }

    public String getReferenceSeed1() {
        return referenceSeed1;
    }

    public void setReferenceSeed1(String referenceSeed1) {
        this.referenceSeed1 = referenceSeed1;
    }

    public BigDecimal getTmpWinAmount2() {
        return BigDecimalUtils.getNotNull(tmpWinAmount2);
    }

    public void setTmpWinAmount2(BigDecimal tmpWinAmount2) {
        this.tmpWinAmount2 = tmpWinAmount2;
    }

    public long getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }
}
