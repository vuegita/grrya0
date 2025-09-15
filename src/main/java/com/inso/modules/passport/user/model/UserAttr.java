package com.inso.modules.passport.user.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.Date;

public class UserAttr {

    private long userid;
    private String username;

    /*** money表数据-只有后台列表查询时才有 ***/
    private BigDecimal freeze;
    /*** money表数据-只有后台列表查询时才有 ***/
    private BigDecimal codeAmount;
    /*** money表数据-只有后台列表查询时才有 ***/
    private BigDecimal balance;

    /*** money表数据-只有后台列表查询时才有 ***/
    private BigDecimal limitAmount;
    /*** money表数据-只有后台列表查询时才有 ***/
    private BigDecimal limitCode;

    /*** 直属代理上级 ***/
    private long agentid;
    private String agentname;

    /*** 直属员工上级 ***/
    private long directStaffid;
    private String directStaffname;

    /*** 邀请我的人用户ID ***/
    private String parentname;
    private long parentid;

    /*** 祖父级上级 ***/
    private String grantfathername;
    private long grantfatherid;

    private String returnLevelStatus;
    private BigDecimal returnLv1Rate;
    private BigDecimal returnLv2Rate;
    private BigDecimal receivLv1Rate;
    private BigDecimal receivLv2Rate;

    /*** 返佣比例-整数 0 - 100 ***/
    private float returnWater;
    /*** 首次充值订单号 ***/
    private String firstRechargeOrderno;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date firstRechargeTime;
    private BigDecimal firstRechargeAmount;

    /*** 邀请好友并完成充值，赠送总金额 ***/
    private BigDecimal inviteFriendTotalAmount;

    /*** 用户类别 ***/
    private String level;
    /*** 用户信息备注 ***/
    private String remark;

    private String userRemark;
    /*** 最后登录时间 ***/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date userLastlogintime;

    private String agentAdminLoginStatus;




    /*** 注册时间-只有查询所有会员时才有 ***/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date regtime;

    /*** 总充值金额 ***/
    private BigDecimal totalRecharge;
    /*** 总提现金额 ***/
    private BigDecimal totalWithdraw;
    /*** 总提现退款金额 ***/
    private BigDecimal totalRefund;
    private String fundKey;
    private String currency;

    private long level1Count;
    private long level2Count;

    private String userType;


    public static String getColumnPrefix(){
        return "attr";
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getDirectStaffid() {
        return directStaffid;
    }

    public void setDirectStaffid(long directStaffid) {
        this.directStaffid = directStaffid;
    }

    public String getFirstRechargeOrderno() {
        return firstRechargeOrderno;
    }

    public void setFirstRechargeOrderno(String firstRechargeOrderno) {
        this.firstRechargeOrderno = firstRechargeOrderno;
    }

    public String getDirectStaffname() {
        return directStaffname;
    }

    public void setDirectStaffname(String directStaffname) {
        this.directStaffname = directStaffname;
    }

    public long getParentid() {
        return parentid;
    }

    public void setParentid(long parentid) {
        this.parentid = parentid;
    }

    public float getReturnWater() {
        return returnWater;
    }

    public void setReturnWater(float returnWater) {
        this.returnWater = returnWater;
    }

    public String getParentname() {
        return parentname;
    }

    public void setParentname(String parentname) {
        this.parentname = parentname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getFreeze() {
        return freeze;
    }

    public void setFreeze(BigDecimal freeze) {
        this.freeze = freeze;
    }

    public BigDecimal getCodeAmount() {
        return codeAmount;
    }

    public void setCodeAmount(BigDecimal codeAmount) {
        this.codeAmount = codeAmount;
    }

    public String getGrantfathername() {
        return grantfathername;
    }

    public void setGrantfathername(String grantfathername) {
        this.grantfathername = grantfathername;
    }

    public long getGrantfatherid() {
        return grantfatherid;
    }

    public void setGrantfatherid(long grantfatherid) {
        this.grantfatherid = grantfatherid;
    }

    public BigDecimal getInviteFriendTotalAmount() {
        return inviteFriendTotalAmount;
    }

    public void setInviteFriendTotalAmount(BigDecimal inviteFriendTotalAmount) {
        this.inviteFriendTotalAmount = inviteFriendTotalAmount;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public Date getFirstRechargeTime() {
        return firstRechargeTime;
    }

    public void setFirstRechargeTime(Date firstRechargeTime) {
        this.firstRechargeTime = firstRechargeTime;
    }

    public Date getRegtime() {
        return regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
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

    public BigDecimal getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
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

    public long getLevel1Count() {
        return level1Count;
    }

    public void setLevel1Count(long level1Count) {
        this.level1Count = level1Count;
    }

    public long getLevel2Count() {
        return level2Count;
    }

    public void setLevel2Count(long level2Count) {
        this.level2Count = level2Count;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public Date getUserLastlogintime() {
        return userLastlogintime;
    }

    public void setUserLastlogintime(Date userLastlogintime) {
        this.userLastlogintime = userLastlogintime;
    }

    public String getAgentAdminLoginStatus() {
        return agentAdminLoginStatus;
    }

    public void setAgentAdminLoginStatus(String agentAdminLoginStatus) {
        this.agentAdminLoginStatus = agentAdminLoginStatus;
    }

    public BigDecimal getFirstRechargeAmount() {
        return BigDecimalUtils.getNotNull(firstRechargeAmount);
    }

    public void setFirstRechargeAmount(BigDecimal firstRechargeAmount) {
        this.firstRechargeAmount = firstRechargeAmount;
    }

    public String getReturnLevelStatus() {
        return returnLevelStatus;
    }

    public void setReturnLevelStatus(String returnLevelStatus) {
        this.returnLevelStatus = returnLevelStatus;
    }

    public BigDecimal getReturnLv1Rate() {
        return returnLv1Rate;
    }

    public void setReturnLv1Rate(BigDecimal returnLv1Rate) {
        this.returnLv1Rate = returnLv1Rate;
    }

    public BigDecimal getReturnLv2Rate() {
        return returnLv2Rate;
    }

    public void setReturnLv2Rate(BigDecimal returnLv2Rate) {
        this.returnLv2Rate = returnLv2Rate;
    }

    public BigDecimal getReceivLv1Rate() {
        return receivLv1Rate;
    }

    public void setReceivLv1Rate(BigDecimal receivLv1Rate) {
        this.receivLv1Rate = receivLv1Rate;
    }

    public BigDecimal getReceivLv2Rate() {
        return receivLv2Rate;
    }

    public void setReceivLv2Rate(BigDecimal receivLv2Rate) {
        this.receivLv2Rate = receivLv2Rate;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getLimitCode() {
        return limitCode;
    }

    public void setLimitCode(BigDecimal limitCode) {
        this.limitCode = limitCode;
    }
}
