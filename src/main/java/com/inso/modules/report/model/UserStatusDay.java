package com.inso.modules.report.model;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

public class UserStatusDay {

    /**
     day_pdate	 				date NOT NULL ,
     day_agentid 	            int(11) NOT NULL DEFAULT 0 comment '所属代理id',
     day_staffid	            int(11) NOT NULL DEFAULT 0,

     day_register_count   		int(11) NOT NULL DEFAULT 0 comment '注册人数',
     day_split_count   		int(11) NOT NULL DEFAULT 0 comment '分裂人数',

     day_total_recharge_count  int(11) NOT NULL DEFAULT 0 comment '充值总次数',
     day_real_recharge_count   int(11) NOT NULL DEFAULT 0 comment '实际成功充值次数',
     day_user_recharge_count   int(11) NOT NULL DEFAULT 0 comment '充值人数',

     day_total_withdraw_count  int(11) NOT NULL DEFAULT 0 comment '提现总次数',
     day_real_withdraw_count   int(11) NOT NULL DEFAULT 0 comment '实际成功提现次数',
     day_user_withdraw_count   int(11) NOT NULL DEFAULT 0 comment '提现人数',

     */

    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;

    private long registerCount;
    private long splitCount;
    private long activeCount;

    private long totalRechargeCount;
    private long userRechargeCount;
    private BigDecimal totalRechargeAmount;

    private long totalWithdrawCount;
    private long userWithdrawCount;
    private BigDecimal totalWithdrawAmount;
    private BigDecimal totalWithdrawFeemoney;

    private long firstRechargeCount;
    private BigDecimal firstRechargeAmount;

    //
    private BigDecimal totalMemberBalance;

    private transient boolean writeToCache = false;

    public void increRegister(long registerCount, long splitCount)
    {
        this.registerCount += registerCount;
        this.splitCount += splitCount;
    }

    public void increActive(long activeCount)
    {
        this.activeCount += activeCount;
    }

    public void increRecharge(long totalRechargeCount, long userRechargeCount, BigDecimal amount)
    {
        this.totalRechargeCount += totalRechargeCount;
        this.userRechargeCount += userRechargeCount;
        this.totalRechargeAmount = getTotalRechargeAmount().add(amount);
    }

    public void increWithdraw(long totalWithdrawCount, long userWithdrawCount, BigDecimal amount, BigDecimal feemoney)
    {
        this.totalWithdrawCount += totalWithdrawCount;
        this.userWithdrawCount += userWithdrawCount;

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        this.totalWithdrawAmount = getTotalWithdrawAmount().add(amount);

        if(feemoney == null || feemoney.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        this.totalWithdrawFeemoney = getTotalWithdrawFeemoney().add(feemoney);
    }

    public void increFirstRecharge(BigDecimal rechargeAmount)
    {
        if(rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        this.firstRechargeCount ++;
        this.firstRechargeAmount = getFirstRechargeAmount().add(rechargeAmount);
    }

    public void merge(UserStatusDay entity)
    {
        this.registerCount += entity.getRegisterCount();
        this.splitCount += entity.getSplitCount();
        this.activeCount += entity.getActiveCount();

        this.totalRechargeCount += entity.getTotalRechargeCount();
        this.userRechargeCount += entity.getUserRechargeCount();
        this.totalRechargeAmount = this.getTotalRechargeAmount().add(entity.getTotalRechargeAmount());

        this.totalWithdrawCount += entity.getTotalWithdrawCount();
        this.userWithdrawCount += entity.getUserWithdrawCount();
        this.totalWithdrawAmount = this.getTotalWithdrawAmount().add(entity.getTotalWithdrawAmount());
        this.totalWithdrawFeemoney = this.getTotalWithdrawFeemoney().add(entity.getTotalWithdrawFeemoney());
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

    public long getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(long registerCount) {
        this.registerCount = registerCount;
    }

    public long getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(long splitCount) {
        this.splitCount = splitCount;
    }

    public long getTotalRechargeCount() {
        return totalRechargeCount;
    }

    public void setTotalRechargeCount(long totalRechargeCount) {
        this.totalRechargeCount = totalRechargeCount;
    }

    public long getUserRechargeCount() {
        return userRechargeCount;
    }

    public void setUserRechargeCount(long userRechargeCount) {
        this.userRechargeCount = userRechargeCount;
    }


    public long getTotalWithdrawCount() {
        return totalWithdrawCount;
    }

    public void setTotalWithdrawCount(long totalWithdrawCount) {
        this.totalWithdrawCount = totalWithdrawCount;
    }

    public long getUserWithdrawCount() {
        return userWithdrawCount;
    }

    public void setUserWithdrawCount(long userWithdrawCount) {
        this.userWithdrawCount = userWithdrawCount;
    }


    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public long getFirstRechargeCount() {
        return firstRechargeCount;
    }

    public void setFirstRechargeCount(long firstRechargeCount) {
        this.firstRechargeCount = firstRechargeCount;
    }

    public BigDecimal getFirstRechargeAmount() {
        return BigDecimalUtils.getNotNull(firstRechargeAmount);
    }

    public void setFirstRechargeAmount(BigDecimal firstRechargeAmount) {
        this.firstRechargeAmount = firstRechargeAmount;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(long activeCount) {
        this.activeCount = activeCount;
    }

    public BigDecimal getTotalRechargeAmount() {
        return BigDecimalUtils.getNotNull(totalRechargeAmount);
    }

    public void setTotalRechargeAmount(BigDecimal totalRechargeAmount) {
        this.totalRechargeAmount = totalRechargeAmount;
    }

    public BigDecimal getTotalWithdrawAmount() {
        return BigDecimalUtils.getNotNull(totalWithdrawAmount);
    }

    public void setTotalWithdrawAmount(BigDecimal totalWithdrawAmount) {
        this.totalWithdrawAmount = totalWithdrawAmount;
    }

    public BigDecimal getTotalWithdrawFeemoney() {
        return BigDecimalUtils.getNotNull(totalWithdrawFeemoney);
    }

    public void setTotalWithdrawFeemoney(BigDecimal totalWithdrawFeemoney) {
        this.totalWithdrawFeemoney = totalWithdrawFeemoney;
    }

    public boolean isWriteToCache() {
        return writeToCache;
    }

    public void setWriteToCache(boolean writeToCache) {
        this.writeToCache = writeToCache;
    }

    public BigDecimal getTotalMemberBalance() {
        return totalMemberBalance;
    }

    public void setTotalMemberBalance(BigDecimal totalMemberBalance) {
        this.totalMemberBalance = totalMemberBalance;
    }
}
