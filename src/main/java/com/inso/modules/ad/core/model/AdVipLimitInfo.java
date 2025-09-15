package com.inso.modules.ad.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class AdVipLimitInfo {



    /**
     *   limit_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *   limit_vipid                  int(11) NOT NULL comment '',
     *   limit_total_money_of_day     decimal(18,2) NOT NULL DEFAULT 0 comment '每天可以赚最大金额',
     *   limit_free_money_of_day      decimal(18,2) NOT NULL DEFAULT 0 comment '每天可以赚免费任务金额',
     *   limit_invite_count_of_day    int(11) NOT NULL DEFAULT 0 comment '不免费的任务需要成功邀请好友才能接着往下做',
     *   limit_force_buy_vip          varchar(50) NOT NULL comment '对于邀请的好友是否强制要买vip才能算免费等到额度: enable|disable',
     *   limit_max_money_of_single    decimal(18,2) NOT NULL DEFAULT 0 comment '单笔可以做最大金额,不能超过免费额度1/5',
     *   limit_createtime             datetime DEFAULT NULL comment '创建时间',
     */

    private long id;
    private long vipid;
    private String vipName;
    private long vipLevel;
    private BigDecimal totalMoneyOfDay;
    private BigDecimal freeMoneyOfDay;
    private long inviteCountOfDay;
    private BigDecimal inviteMoneyOfDay;

    private long buyCountOfDay;
    private BigDecimal buyMoneyOfDay;
    private BigDecimal maxMoneyOfSingle;

    private BigDecimal lv1RebateBalanceRate;
    private BigDecimal lv2RebateBalanceRate;
    private BigDecimal lv1RebateWithdrawlRate;
    private BigDecimal lv2RebateWithdrawlRate;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    private BigDecimal price;
    private long paybackPeriod;

    public static String getColumnPrefix(){
        return "limit";
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVipid() {
        return vipid;
    }

    public void setVipid(long vipid) {
        this.vipid = vipid;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public long getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(long vipLevel) {
        this.vipLevel = vipLevel;
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

    public BigDecimal getMaxMoneyOfSingle() {
        return maxMoneyOfSingle;
    }

    public void setMaxMoneyOfSingle(BigDecimal maxMoneyOfSingle) {
        this.maxMoneyOfSingle = maxMoneyOfSingle;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @JSONField(serialize = false, deserialize = false)
    public static BigDecimal calcTotalMoneyOfDay(BigDecimal vipPrice, long paybackPeriod)
    {
        if(paybackPeriod <= 0)
        {
            paybackPeriod = 50;
        }
        // 35天回本
        BigDecimal rs = vipPrice.divide(new BigDecimal(paybackPeriod), 2, RoundingMode.HALF_UP);
        rs = safeMoneyValue(rs);
        return rs;
    }

    @JSONField(serialize = false, deserialize = false)
    public static BigDecimal calcMaxMoneyOfSingle(BigDecimal totalMoneyOfDay)
    {
        BigDecimal rs = totalMoneyOfDay.divide(BigDecimalUtils.DEF_5, 2, RoundingMode.HALF_UP);
        rs = safeMoneyValue(rs);
        if(rs.compareTo(BigDecimalUtils.DEF_200) > 0)
        {
            rs = BigDecimalUtils.DEF_200;
        }
        return rs;
    }

    @JSONField(serialize = false, deserialize = false)
    public static BigDecimal calcFreeMoneyOfSingle(BigDecimal totalMoneyOfDay)
    {
        // 70%
        BigDecimal rs = totalMoneyOfDay.multiply(BigDecimalUtils.DEF_7).divide(BigDecimalUtils.DEF_10, 2, RoundingMode.HALF_UP);
        return safeMoneyValue(rs);
    }

    private static BigDecimal safeMoneyValue(BigDecimal val)
    {
//        int rsValue = val.intValue();
//        if(rsValue % 5 != 0)
//        {
//            // 5的整数倍
//            val = new BigDecimal((rsValue / 5 + 1) * 5);
//        }
        return val;
    }

    public long getPaybackPeriod() {
        return paybackPeriod;
    }

    public void setPaybackPeriod(long paybackPeriod) {
        this.paybackPeriod = paybackPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getInviteMoneyOfDay() {
        return inviteMoneyOfDay;
    }

    public void setInviteMoneyOfDay(BigDecimal inviteMoneyOfDay) {
        this.inviteMoneyOfDay = inviteMoneyOfDay;
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

    public BigDecimal getLv1RebateBalanceRate() {
        return lv1RebateBalanceRate;
    }

    public void setLv1RebateBalanceRate(BigDecimal lv1RebateBalanceRate) {
        this.lv1RebateBalanceRate = lv1RebateBalanceRate;
    }

    public BigDecimal getLv2RebateBalanceRate() {
        return lv2RebateBalanceRate;
    }

    public void setLv2RebateBalanceRate(BigDecimal lv2RebateBalanceRate) {
        this.lv2RebateBalanceRate = lv2RebateBalanceRate;
    }

    public BigDecimal getLv1RebateWithdrawlRate() {
        return lv1RebateWithdrawlRate;
    }

    public void setLv1RebateWithdrawlRate(BigDecimal lv1RebateWithdrawlRate) {
        this.lv1RebateWithdrawlRate = lv1RebateWithdrawlRate;
    }

    public BigDecimal getLv2RebateWithdrawlRate() {
        return lv2RebateWithdrawlRate;
    }

    public void setLv2RebateWithdrawlRate(BigDecimal lv2RebateWithdrawlRate) {
        this.lv2RebateWithdrawlRate = lv2RebateWithdrawlRate;
    }
}
