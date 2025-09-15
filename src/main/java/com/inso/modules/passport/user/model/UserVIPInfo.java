package com.inso.modules.passport.user.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.Date;

public class UserVIPInfo {

    public static final int DEFAULT_MAX_ADD_CARD_SIZE = 3;

    /*** 默认是否开启VIP ***/
    public static final boolean DEFAULT_ENABLE_EXPIRES_VIP = false;

    /**
     uv_id       			int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     uv_userid       		int(11) UNSIGNED NOT NULL ,
     uv_vip_type       	varchar(50) NOT NULL comment 'vip 类型,检举类型',
     uv_vipid       		int(11) UNSIGNED NOT NULL ,
     uv_status             varchar(50) NOT NULL comment 'enable|disable',
     uv_expires_time       datetime DEFAULT NULL comment '过期时间-保留参数',
     uv_createtime         datetime DEFAULT NULL comment '时间',
     */

    private long id;
    private long userid;
    private String username;
    private long agentid;
    private String agentname;
    private long staffid;
    private String staffname;
    private long vipid;
    private long vipLevel;
    private String vipType;
    private String vipName;
    private String status;
    private BigDecimal maxMoneyOfSingle;

    private BigDecimal totalMoneyOfDay;
    private BigDecimal freeMoneyOfDay;
    private long inviteCountOfDay;
    private BigDecimal inviteMoneyOfDay;
    private BigDecimal UserTotalMoneyOfDay;

    private long buyCountOfDay;
    private BigDecimal buyMoneyOfDay;

    /*** 提现额度 ***/
    private BigDecimal withdrawlAmount = BigDecimal.ZERO;


    private int todayInviteCount;
    private int todayInviteRegAndBuyCount;

    /*** 是否开启VIP失效, 默认不开启 ***/
    private boolean enableExpires = DEFAULT_ENABLE_EXPIRES_VIP;


    @JSONField(format = "yyyy-MM-dd")
    private Date begintime;
    @JSONField(format = "yyyy-MM-dd")
    private Date expirestime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;


    /*** money表数据-只有后台列表查询时才有 ***/
    private BigDecimal balance;
    /*** 总充值金额 ***/
    private BigDecimal totalRecharge;
    /*** 总提现金额 ***/
    private BigDecimal totalWithdraw;
    /*** 总提现退款金额 ***/


    public static String getColumnPrefix(){
        return "uv";
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



    public long getBuyCountOfDay() {
        return buyCountOfDay;
    }

    public void setBuyCountOfDay(long buyCountOfDay) {
        this.buyCountOfDay = buyCountOfDay;
    }

    public BigDecimal getBuyMoneyOfDay() {
        return buyMoneyOfDay;
    }

    public void setBuyMoneyOfDay(BigDecimal buyMoneyOfDay) {
        this.buyMoneyOfDay = buyMoneyOfDay;
    }

    public BigDecimal getMaxMoneyOfSingle() {
        return maxMoneyOfSingle;
    }

    public void setMaxMoneyOfSingle(BigDecimal maxMoneyOfSingle) {
        this.maxMoneyOfSingle = maxMoneyOfSingle;
    }

    public BigDecimal getTotalMoneyOfDay() {
        return totalMoneyOfDay;
    }

    public void setTotalMoneyOfDay(BigDecimal totalMoneyOfDay) {
        this.totalMoneyOfDay = totalMoneyOfDay;
    }

    public BigDecimal getFreeMoneyOfDay() {
        return freeMoneyOfDay;
    }

    public void setFreeMoneyOfDay(BigDecimal freeMoneyOfDay) {
        this.freeMoneyOfDay = freeMoneyOfDay;
    }

    public long getInviteCountOfDay() {
        return inviteCountOfDay;
    }

    public void setInviteCountOfDay(long inviteCountOfDay) {
        this.inviteCountOfDay = inviteCountOfDay;
    }

    public BigDecimal getInviteMoneyOfDay() {
        return inviteMoneyOfDay;
    }

    public void setInviteMoneyOfDay(BigDecimal inviteMoneyOfDay) {
        this.inviteMoneyOfDay = inviteMoneyOfDay;
    }

    public BigDecimal getUserTotalMoneyOfDay() {
        return UserTotalMoneyOfDay;
    }

    public void setUserTotalMoneyOfDay(BigDecimal UserTotalMoneyOfDay) {
        this.UserTotalMoneyOfDay = UserTotalMoneyOfDay;
    }

    public int getTodayInviteCount() {
        return todayInviteCount;
    }

    public void setTodayInviteCount(int todayInviteCount) {
        this.todayInviteCount = todayInviteCount;
    }

    public int getTodayInviteRegAndBuyCount() {
        return todayInviteRegAndBuyCount;
    }

    public void setTodayInviteRegAndBuyCount(int todayInviteRegAndBuyCount) {
        this.todayInviteRegAndBuyCount = todayInviteRegAndBuyCount;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getVipid() {
        return vipid;
    }

    public void setVipid(long vipid) {
        this.vipid = vipid;
    }

    public String getVipType() {
        return vipType;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public Date getExpirestime() {
        return expirestime;
    }

    public void setExpirestime(Date expirestime) {
        this.expirestime = expirestime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public long getStaffid() {
        return staffid;
    }

    public void setStaffid(long staffid) {
        this.staffid = staffid;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean verifyBuyVIP()
    {
//        getExpirestime().getTime() > System.currentTimeMillis()
        return id > 0 && vipLevel > 0 && Status.ENABLE.getKey().equalsIgnoreCase(status);
    }

    /**
     * 验证VIP是否过期
     * @return
     */
    @JSONField(serialize = false, deserialize = false)
    public boolean verifyExpires()
    {
        // 默认关闭,不开启
        return enableExpires && getExpirestime().getTime() <= System.currentTimeMillis();
    }

    public long getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(long vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public boolean isEnableExpires() {
        return enableExpires;
    }

    public void setEnableExpires(boolean enableExpires) {
        this.enableExpires = enableExpires;
    }

    public BigDecimal getWithdrawlAmount() {
        return withdrawlAmount;
    }

    public void setWithdrawlAmount(BigDecimal withdrawlAmount) {
        this.withdrawlAmount = withdrawlAmount;
    }
}
