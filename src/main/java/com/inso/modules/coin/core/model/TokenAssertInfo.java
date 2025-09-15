package com.inso.modules.coin.core.model;


import com.inso.modules.common.model.CryptoCurrency;

public class TokenAssertInfo {

    private CryptoNetworkType networkType;
    private CryptoCurrency currencyType;
    private String contractAddress;
    private int decimals;
    private String approveMethod;

    public CryptoCurrency getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CryptoCurrency currencyType) {
        this.currencyType = currencyType;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public CryptoNetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(CryptoNetworkType networkType) {
        this.networkType = networkType;
    }

    public String getApproveMethod() {
        return approveMethod;
    }

    public void setApproveMethod(String approveMethod) {
        this.approveMethod = approveMethod;
    }
}
