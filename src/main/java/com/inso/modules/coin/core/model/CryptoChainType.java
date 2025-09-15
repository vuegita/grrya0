package com.inso.modules.coin.core.model;

public enum CryptoChainType {

    ETH("ETH", false),
    ERC_20("ERC-20", true),

//    HT("HT", false,"https://cn.etherscan.com/tokenholdings?a="),
    BNB("BNB", false),
    BEP_20("BEP-20", true),


    TRX("TRX", false),
    TRC_20("TRC-20", true),

    MATIC("MATIC", true),
    ;

    private String key;
    /*** 是不是代币Token ***/
    private boolean mIsToken;

    CryptoChainType(String key, boolean isToken)
    {
        this.key = key;
        this.mIsToken = isToken;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isToken() {
        return mIsToken;
    }

    public static CryptoChainType getType(String key)
    {
        CryptoChainType[] values = CryptoChainType.values();
        for(CryptoChainType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
