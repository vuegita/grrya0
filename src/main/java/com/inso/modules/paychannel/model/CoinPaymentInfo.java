package com.inso.modules.paychannel.model;

import java.math.BigDecimal;

public class CoinPaymentInfo {

    private long channelid;
    private String channelType;

    private String accountPrivateKey;
    private String accountAddress;

    private String agentName;

    private String networkType;
    /*** 当前通道支持币种 ***/
    private String currencyTypeArr;
    private String chainType;

    private BigDecimal gasLimit;

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getAccountPrivateKey() {
        return accountPrivateKey;
    }

    public void setAccountPrivateKey(String accountPrivateKey) {
        this.accountPrivateKey = accountPrivateKey;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public long getChannelid() {
        return channelid;
    }

    public void setChannelid(long channelid) {
        this.channelid = channelid;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public BigDecimal getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigDecimal gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getCurrencyTypeArr() {
        return currencyTypeArr;
    }

    public void setCurrencyTypeArr(String currencyTypeArr) {
        this.currencyTypeArr = currencyTypeArr;
    }
}
