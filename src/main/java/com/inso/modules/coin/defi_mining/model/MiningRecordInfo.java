package com.inso.modules.coin.defi_mining.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.Date;

public class MiningRecordInfo {

    /**
     record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

     record_product_id             int(11) NOT NULL ,
     record_account_id             int(11) NOT NULL ,

     record_userid                 int(11) NOT NULL ,
     record_username 	            varchar(255) NOT NULL comment '用户名',
     record_address 	            varchar(255) NOT NULL comment '用户地址',

     record_reward_amount          decimal(25,8) NOT NULL DEFAULT 0 comment '收益金额',

     record_status                varchar(20) NOT NULL comment '状态',
     record_createtime  	        datetime DEFAULT NULL ,
     record_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;
    private long productId;

    /*** 合约ID ***/
    private long contractid;

    private String networkType;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal expectedRate;
    private BigDecimal minWalletBalance;

    private long userid;
    private String username;
    private String address;

    // staking
    private String stakingStatus;
    private String stakingSettleMode;
    private BigDecimal stakingAmount;
    private BigDecimal stakingRewardValue;
    private BigDecimal stakingRewardExternal;
    private long stakingRewardHour;

    // voucher
    private BigDecimal voucherNodeValue;
    private String voucherNodeSettleMode;
    private BigDecimal voucherStakingValue;

    private BigDecimal moneyBalance;
    private BigDecimal coldAmount;
    /*** ***/
    private BigDecimal walletBalance;

    /*** 自动提现到余额 ***/
//    private BigDecimal rewardBalance;
    private BigDecimal totalRewardAmount;


    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;




    public static String getColumnPrefix(){
        return "record";
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public BigDecimal getExpectedRate() {
        return expectedRate;
    }

    public void setExpectedRate(BigDecimal expectedRate) {
        this.expectedRate = expectedRate;
    }

    public long getContractid() {
        return contractid;
    }

    public void setContractid(long contractid) {
        this.contractid = contractid;
    }

    public BigDecimal getMinWalletBalance() {
        return minWalletBalance;
    }

    public void setMinWalletBalance(BigDecimal minWalletBalance) {
        this.minWalletBalance = minWalletBalance;
    }

//    public BigDecimal getRewardBalance() {
//        return rewardBalance;
//    }
//
//    public void setRewardBalance(BigDecimal rewardBalance) {
//        this.rewardBalance = rewardBalance;
//    }

    public BigDecimal getTotalRewardAmount() {
        return totalRewardAmount;
    }

    public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
        this.totalRewardAmount = totalRewardAmount;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public BigDecimal getColdAmount() {
        return coldAmount;
    }

    public void setColdAmount(BigDecimal coldAmount) {
        this.coldAmount = coldAmount;
    }

    public String getStakingStatus() {
        return stakingStatus;
    }

    public void setStakingStatus(String stakingStatus) {
        this.stakingStatus = stakingStatus;
    }

    public BigDecimal getStakingAmount() {
        return stakingAmount;
    }

    public void setStakingAmount(BigDecimal stakingAmount) {
        this.stakingAmount = stakingAmount;
    }

    public long getStakingRewardHour() {
        return stakingRewardHour;
    }

    public void setStakingRewardHour(long stakingRewardHour) {
        this.stakingRewardHour = stakingRewardHour;
    }

    public BigDecimal getStakingRewardValue() {
        return stakingRewardValue;
    }

    public void setStakingRewardValue(BigDecimal stakingRewardValue) {
        this.stakingRewardValue = stakingRewardValue;
    }

    public String getStakingSettleMode() {
        return stakingSettleMode;
    }

    public void setStakingSettleMode(String stakingSettleMode) {
        this.stakingSettleMode = stakingSettleMode;
    }

    public BigDecimal getVoucherNodeValue() {
        return BigDecimalUtils.getNotNull(voucherNodeValue);
    }

    public void setVoucherNodeValue(BigDecimal voucherNodeValue) {
        this.voucherNodeValue = voucherNodeValue;
    }

    public String getVoucherNodeSettleMode() {
        return voucherNodeSettleMode;
    }

    public void setVoucherNodeSettleMode(String voucherNodeSettleMode) {
        this.voucherNodeSettleMode = voucherNodeSettleMode;
    }

    public BigDecimal getVoucherStakingValue() {
        return BigDecimalUtils.getNotNull(voucherStakingValue);
    }

    public void setVoucherStakingValue(BigDecimal voucherStakingValue) {
        this.voucherStakingValue = voucherStakingValue;
    }

    public BigDecimal getStakingRewardExternal() {
        return stakingRewardExternal;
    }

    public void setStakingRewardExternal(BigDecimal stakingRewardExternal) {
        this.stakingRewardExternal = stakingRewardExternal;
    }


    public void handleTotalReward()
    {
        this.totalRewardAmount = BigDecimalUtils.getNotNull(this.totalRewardAmount);
        this.stakingRewardExternal = BigDecimalUtils.getNotNull(this.stakingRewardExternal);
        this.totalRewardAmount = this.totalRewardAmount.add(this.stakingRewardExternal);
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
}
