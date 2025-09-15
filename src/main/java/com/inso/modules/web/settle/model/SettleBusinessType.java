package com.inso.modules.web.settle.model;

import org.springframework.ui.Model;

public enum SettleBusinessType {

    COIN_WITHDRAW_DAY("coin_withdraw_day"),
    COIN_WITHDRAW_MONTH("coin_withdraw_month"),

    ;
    private String key;

    SettleBusinessType(String key)
    {
        this.key = key;

    }

    public String getKey() {
        return key;
    }


    public static SettleBusinessType getType(String key)
    {
        SettleBusinessType[] values = SettleBusinessType.values();
        for(SettleBusinessType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }


    public static void addModel(Model model)
    {
        model.addAttribute("settleBusinessTypeArr", SettleBusinessType.values());
    }

}
