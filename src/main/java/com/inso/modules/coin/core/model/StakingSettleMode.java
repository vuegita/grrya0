package com.inso.modules.coin.core.model;

import org.springframework.ui.Model;

public enum StakingSettleMode {

    DEF("Default", "默认不结算"),
    BALANCE("Balance", "结算到余额"), // 结算到系统余额里面

    ;

    private String key;
    private String name;

    StakingSettleMode(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static StakingSettleMode getType(String key)
    {
        StakingSettleMode[] values = StakingSettleMode.values();
        for(StakingSettleMode type : values)
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
        model.addAttribute("stakingSettleArr", StakingSettleMode.values());
    }


}
