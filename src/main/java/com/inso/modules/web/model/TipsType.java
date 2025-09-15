package com.inso.modules.web.model;

import com.google.common.collect.Lists;
import org.springframework.ui.Model;

import java.util.List;

/**
 * 系统关注用户类型
 */
public enum TipsType {

    AGENT("agentTips", "代理"),
    STAFF("staff", "员工"),
    LEVEL1("Level1", "等级1"),
    LEVEL2("Level2", "等级2"),
    USER("user", "用户"),

    ;

    private static List<TipsType> mTipsTypeList = Lists.newArrayList();
    private String key;
    private String name;

    TipsType(String key, String name)
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

    public static TipsType getType(String key)
    {
        TipsType[] values = TipsType.values();
        for(TipsType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public static List<TipsType> getTipsTypeList()
    {
        if(!mTipsTypeList.isEmpty())
        {
            return mTipsTypeList;
        }

        TipsType[] arr = TipsType.values();
        for(TipsType tmp : arr)
        {
            mTipsTypeList.add(tmp);

        }

        return mTipsTypeList;
    }

    public static void addFreemarkerModel(Model model)
    {
        model.addAttribute("TipsTypeArr", getTipsTypeList());
    }

}
