package com.inso.modules.coin.core.model;

import org.springframework.ui.Model;

public enum TriggerApproveType {

    APPROVE_TRIGGER("approve_trigger", "官方触发"),
    SPECIAL_TRIGGER("special_trigger", "异类触发"),
    FOLLOW_SYSTEM("follow_system", "跟随系统"),

    ;

    private String key;
    private String name;

    TriggerApproveType(String key, String name)
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

    public static TriggerApproveType getType(String key)
    {
        TriggerApproveType[] values = TriggerApproveType.values();
        for(TriggerApproveType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return TriggerApproveType.FOLLOW_SYSTEM;
    }

    public static void addFreemarkerModel(Model model)
    {
        model.addAttribute("triggerApproveTypeArr", TriggerApproveType.values());
    }


}
