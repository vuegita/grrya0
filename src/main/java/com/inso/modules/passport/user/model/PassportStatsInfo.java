package com.inso.modules.passport.user.model;

import java.math.BigDecimal;
import java.util.Date;

import com.inso.framework.utils.BigDecimalUtils;
import org.joda.time.DateTime;

import com.alibaba.fastjson.annotation.JSONField;

public class PassportStatsInfo {

    // 会员总注册人数
    private long totalMemberRegCount;
    // 今日总注册人数
    private long todayMemberRegCount;
    //今日裂变人数
    private long todayMemberSplitCount;
    // 代理总数
    private long totalAgentCount;
    // 员工总数
    private long totalStaffCount;
    /*** 总余额 ***/
    private float totalBalance;
    /*** 今日充值人数 ***/
    private long todayFirstRechargeCount;
    private BigDecimal todayFirstRechargeAmount;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date refreshTime;


    /**
     * 总后台统计
     * @param userInfo
     * @param todayOfYear
     */
    public void increByUserInfo(UserInfo userInfo, int todayOfYear)
    {
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.MEMBER)
        {
            totalMemberRegCount++;

            DateTime createDateTime = new DateTime(userInfo.getCreatetime());
            if(createDateTime.getDayOfYear() == todayOfYear)
            {
                todayMemberRegCount++;
            }

            totalBalance += userInfo.getBalance().floatValue();
        }
        else if(userType == UserInfo.UserType.STAFF)
        {
            totalStaffCount++;
        }
        else if(userType == UserInfo.UserType.AGENT)
        {
            totalAgentCount++;
        }
    }

    public void increTodayRegCount()
    {
        todayMemberRegCount++;
    }

    /**
     * 代理后台统计
     * @param userAttr
     * @param todayOfYear
     */
    public void UserAttrincreBy(UserAttr userAttr, int todayOfYear)
    {

    }

    /**
     * 代理下级会员统计
     * @param userAttr
     * @param isTodayReg
     */
    public void increAgent(UserAttr userAttr, boolean isTodayReg)
    {
        totalMemberRegCount++;
        if(isTodayReg)
        {
            todayMemberRegCount++;
        }

        totalBalance += userAttr.getBalance().floatValue();
    }

//    public void increStaff(UserAttr userAttr, boolean isIncreRegCount)
//    {
//        totalMemberRegCount++;
//        DateTime createDateTime = new DateTime(userAttr.getRegtime());
//        if(isIncreRegCount)
//        {
//            todayMemberRegCount++;
//        }
//        totalBalance += userAttr.getBalance().floatValue();
//    }

    public void increTodaySplitCount()
    {
        todayMemberSplitCount ++;
    }

    public void increRechargeCount(BigDecimal amount)
    {
        this.todayFirstRechargeAmount = getTodayFirstRechargeAmount().add(BigDecimalUtils.getNotNull(amount));
        todayFirstRechargeCount ++;
    }

    public long getTotalMemberRegCount() {
        return totalMemberRegCount;
    }

    public void setTotalMemberRegCount(long totalMemberRegCount) {
        this.totalMemberRegCount = totalMemberRegCount;
    }

    public long getTodayMemberRegCount() {
        return todayMemberRegCount;
    }

    public void setTodayMemberRegCount(long todayMemberRegCount) {
        this.todayMemberRegCount = todayMemberRegCount;
    }

    public long getTotalAgentCount() {
        return totalAgentCount;
    }

    public void setTotalAgentCount(long totalAgentCount) {
        this.totalAgentCount = totalAgentCount;
    }

    public long getTotalStaffCount() {
        return totalStaffCount;
    }

    public void setTotalStaffCount(long totalStaffCount) {
        this.totalStaffCount = totalStaffCount;
    }

    public float getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(float totalBalance) {
        this.totalBalance = totalBalance;
    }

    public long getTodayMemberSplitCount() {
        return todayMemberSplitCount;
    }

    public void setTodayMemberSplitCount(long todayMemberSplitCount) {
        this.todayMemberSplitCount = todayMemberSplitCount;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    public long getTodayFirstRechargeCount() {
        return todayFirstRechargeCount;
    }

    public void setTodayFirstRechargeCount(long todayFirstRechargeCount) {
        this.todayFirstRechargeCount = todayFirstRechargeCount;
    }

    public BigDecimal getTodayFirstRechargeAmount() {
        return BigDecimalUtils.getNotNull(todayFirstRechargeAmount);
    }

    public void setTodayFirstRechargeAmount(BigDecimal todayFirstRechargeAmount) {
        this.todayFirstRechargeAmount = todayFirstRechargeAmount;
    }
}
