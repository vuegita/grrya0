package com.inso.modules.web.promotion_channel.model;

import org.springframework.ui.Model;

public enum PromotionChannelType {

    Youtube("Youtube"),
    Tiktok("Tiktok"),

    ;

    private static final PromotionChannelType[] mArr = PromotionChannelType.values();
    private String key;

    PromotionChannelType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }


    public static PromotionChannelType getType(String key)
    {
        PromotionChannelType[] values = mArr;
        for(PromotionChannelType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public static void addFreemarker(Model model)
    {
        model.addAttribute("typeArr", mArr);
    }



}
