package com.inso.modules.paychannel.model;

public enum PayProductType {

    TAJPAY("Tajpay",  true,true),

    BANK("Bank", true,true),


    // 印度UPI
    UPI("UPI", true,false),

    // 钱包-对应线下支付
    Wallet("Wallet", false,true),

    // 数字货币
    COIN("Coin", false,true),
    FIAT_2_STABLE_COIN("Fiat2StableCoin", false,true),
    ;

    private String key;
    private boolean enablePayin;
    private boolean enablePayout;

    PayProductType(String key, boolean enablePayin, boolean enablePayout)
    {
        this.key = key;
        this.enablePayin = enablePayin;
        this.enablePayout = enablePayout;
    }

    public String getKey() {
        return key;
    }

    public boolean isEnablePayin() {
        return enablePayin;
    }

    public boolean isEnablePayout() {
        return enablePayout;
    }

    public static PayProductType getType(String key)
    {
        PayProductType[] values = PayProductType.values();
        for(PayProductType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
