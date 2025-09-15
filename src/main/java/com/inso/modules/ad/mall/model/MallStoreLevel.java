package com.inso.modules.ad.mall.model;

import org.springframework.ui.Model;

/**
 * 用户等级
 */
public enum MallStoreLevel {
    LV_1("Lv1"),
    LV_2("Lv2"),
    LV_3("Lv3"),
    LV_4("Lv4"),
    LV_5("Lv5"),
    ;

    private String key;

    MallStoreLevel(String key)
    {
        this.key = key;
    }

    private static MallStoreLevel[] arr = MallStoreLevel.values();

    public String getKey()
    {
        return key;
    }

    public static MallStoreLevel getType(String key)
    {
        for(MallStoreLevel type : arr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static void addFreemarker(Model model)
    {
        model.addAttribute("mallStoreLevelArr", arr);
    }
}
