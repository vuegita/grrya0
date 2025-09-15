package com.inso.modules.coin.defi_mining.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class MiningProductInfo {

    /**
     *   product_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     *
     *   product_name 	                varchar(255) NOT NULL comment '产品名称',
     *   product_network_type          varchar(255) NOT NULL comment '网络类型',
     *
     *   product_base_currency         varchar(255) NOT NULL comment 'ERC20-TRC20相关token',
     *   product_quote_currency        varchar(255) NOT NULL comment '收益稳定币=USDT|USDC|USDP',
     *
     *   product_min_withdraw_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '最小提现金额',
     *   product_min_wallet_balance    decimal(25,8) NOT NULL DEFAULT 0 comment '最小钱包余额',
     *   product_expected_rate         decimal(18,3) NOT NULL DEFAULT 0 comment '预期收益率',
     *   product_reward_period         int(11) NOT NULL DEFAULT 0 comment '收益日期',
     *
     *   product_network_type_sort     int(11) NOT NULL DEFAULT 0 comment '网络类型排序',
     *   product_quote_currency_sort    int(11) NOT NULL DEFAULT 0 comment '币种类型',
     *
     *   product_status                varchar(20) NOT NULL comment '状态',
     *   product_createtime  	        datetime DEFAULT NULL ,
     *   product_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    /*** 合约ID ***/
    private long contractid;
    /*** 合约KEY-加密的 ***/
    private String contractKey;

    private String name;
    private String networkType;
    private String chainType;

    private String baseCurrency;
    private String quoteCurrency;

    /*** 挖矿代币地址-queryAll才有 ***/
    private String quoteCurrencyCtrAddr;
    /*** 授权合约地址-queryAll才有 ***/
    private String approveCtrAddress;
    /*** 挖矿代币地址-queryAll才有 ***/
    private BigDecimal rewardBalance;
    private BigDecimal totalRewardAmount;

    private BigDecimal minWithdrawAmount;
    private BigDecimal minWalletBalance;

    private BigDecimal expectedRate;
    private long rewardPeriod;

    private long networkTypeSort;
    private long quoteCurrencySort;

    /*** 判断用户是否流动性挖矿 ***/
    private boolean existUserRecord;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    /*** 质押数据-临时，非model数据 ***/
    private String stakingStatus;
    private BigDecimal stakingAmount;
    private BigDecimal stakingRewardValue;
    private BigDecimal stakingRewardExternal;
    private BigDecimal  stakingExpectedRate;

    private BigDecimal voucherNodeAmount;
    /*** 钱包余额，只有前端调用才有 ***/
    private BigDecimal walletBalance;

    public static String getColumnPrefix(){
        return "product";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public BigDecimal getMinWithdrawAmount() {
        return minWithdrawAmount;
    }

    public void setMinWithdrawAmount(BigDecimal minWithdrawAmount) {
        this.minWithdrawAmount = minWithdrawAmount;
    }

    public BigDecimal getMinWalletBalance() {
        return minWalletBalance;
    }

    public void setMinWalletBalance(BigDecimal minWalletBalance) {
        this.minWalletBalance = minWalletBalance;
    }

    public BigDecimal getExpectedRate() {
        return expectedRate;
    }

    public void setExpectedRate(BigDecimal expectedRate) {
        this.expectedRate = expectedRate;
    }

    public long getRewardPeriod() {
        return rewardPeriod;
    }

    public void setRewardPeriod(long rewardPeriod) {
        this.rewardPeriod = rewardPeriod;
    }

    public long getNetworkTypeSort() {
        return networkTypeSort;
    }

    public void setNetworkTypeSort(long networkTypeSort) {
        this.networkTypeSort = networkTypeSort;
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


    public long getQuoteCurrencySort() {
        return quoteCurrencySort;
    }

    public void setQuoteCurrencySort(long quoteCurrencySort) {
        this.quoteCurrencySort = quoteCurrencySort;
    }

    public boolean isExistUserRecord() {
        return existUserRecord;
    }

    public void setExistUserRecord(boolean existUserRecord) {
        this.existUserRecord = existUserRecord;
    }

    public long getContractid() {
        return contractid;
    }

    public void setContractid(long contractid) {
        this.contractid = contractid;
    }

    public String getContractKey() {
        return contractKey;
    }

    public void setContractKey(String contractKey) {
        this.contractKey = contractKey;
    }

    public String getQuoteCurrencyCtrAddr() {
        return quoteCurrencyCtrAddr;
    }

    public void setQuoteCurrencyCtrAddr(String quoteCurrencyCtrAddr) {
        this.quoteCurrencyCtrAddr = quoteCurrencyCtrAddr;
    }

    public BigDecimal getRewardBalance() {
        return rewardBalance;
    }

    public void setRewardBalance(BigDecimal rewardBalance) {
        this.rewardBalance = rewardBalance;
    }

    public BigDecimal getTotalRewardAmount() {
        return totalRewardAmount;
    }

    public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
        this.totalRewardAmount = totalRewardAmount;
    }

    public String getApproveCtrAddress() {
        return approveCtrAddress;
    }

    public void setApproveCtrAddress(String approveCtrAddress) {
        this.approveCtrAddress = approveCtrAddress;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
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

    public BigDecimal getStakingExpectedRate() {
        return stakingExpectedRate;
    }

    public void setStakingExpectedRate(BigDecimal stakingExpectedRate) {
        this.stakingExpectedRate = stakingExpectedRate;
    }

    public BigDecimal getStakingRewardValue() {
        return stakingRewardValue;
    }

    public void setStakingRewardValue(BigDecimal stakingRewardValue) {
        this.stakingRewardValue = stakingRewardValue;
    }

    public BigDecimal getVoucherNodeAmount() {
        return voucherNodeAmount;
    }

    public void setVoucherNodeAmount(BigDecimal voucherNodeAmount) {
        this.voucherNodeAmount = voucherNodeAmount;
    }

    public BigDecimal getStakingRewardExternal() {
        return stakingRewardExternal;
    }

    public void setStakingRewardExternal(BigDecimal stakingRewardExternal) {
        this.stakingRewardExternal = stakingRewardExternal;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
}
