package com.inso.modules.common.model;

public enum FiatCurrencyType implements ICurrencyType{

    INR("INR"),
    COP("COP"),
    MYR("MYR"),
    BRL("BRL"),

    MNT("MNT"), // 蒙古图格

    USD("USD"),
    CENT("CENT"),
    ;

    private String key;

    private static  FiatCurrencyType[] values = FiatCurrencyType.values();

    FiatCurrencyType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    @Override
    public String getCategory() {
        return "Fiat";
    }

    public static FiatCurrencyType getType(String key)
    {
        for(FiatCurrencyType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }


}
