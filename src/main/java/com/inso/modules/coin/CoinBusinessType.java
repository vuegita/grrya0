package com.inso.modules.coin;

public enum CoinBusinessType {

    APPROVE("approve", 1),
    DEFI_MINING("defi_mining", 2),
    CLOUD_MINING("cloud_mining", 3),
    ;

    private String key;

    private int code;

    CoinBusinessType(String key, int code)
    {
        this.key = key;
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public int getCode() {
        return code;
    }
}
