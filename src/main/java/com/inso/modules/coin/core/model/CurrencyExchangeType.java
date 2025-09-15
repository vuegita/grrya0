package com.inso.modules.coin.core.model;

import org.springframework.ui.Model;

public enum CurrencyExchangeType {

    USD("USD", "稳定币(USD)"),
    FLOAT("Float", "浮动"),
    ;

    private String key;
    private String name;

    CurrencyExchangeType(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public String getName() {
        return name;
    }

    public static CurrencyExchangeType getType(String key)
    {
        CurrencyExchangeType[] values = CurrencyExchangeType.values();
        for(CurrencyExchangeType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static void addFreemakerModel(Model model)
    {
        CurrencyExchangeType[] exchangeRateTypeArr = CurrencyExchangeType.values();
        model.addAttribute("exchangeRateTypeArr", exchangeRateTypeArr);
    }
}
