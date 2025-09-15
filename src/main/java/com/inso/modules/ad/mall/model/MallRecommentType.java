package com.inso.modules.ad.mall.model;

import org.springframework.ui.Model;

/**
 * 用户等级
 */
public enum MallRecommentType {
    Smart("Smart"),
    ;

    private String key;

    MallRecommentType(String key)
    {
        this.key = key;
    }

    private static MallRecommentType[] arr = MallRecommentType.values();

    public String getKey()
    {
        return key;
    }

    public static MallRecommentType getType(String key)
    {
        for(MallRecommentType type : arr)
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
        model.addAttribute("mallRecommentArr", arr);
    }
}
