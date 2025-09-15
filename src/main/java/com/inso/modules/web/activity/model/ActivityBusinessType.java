package com.inso.modules.web.activity.model;

import org.springframework.ui.Model;

public enum ActivityBusinessType {

    INVITE_ACTIVITY(1,"invite_activity","极限邀人活动"),

    ;

    /*** 在这里配置要唯一，按自增 1, 2, 3 ***/
    private int id;
    private String key;
    private String remark;

    ActivityBusinessType(int id, String key, String remark)
    {
        this.key = key;
        this.id = id;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getRemark() {
        return remark;
    }

    public static ActivityBusinessType getType(String key)
    {
        ActivityBusinessType[] values = ActivityBusinessType.values();
        for(ActivityBusinessType type : values)
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
        model.addAttribute("activityBusinessArr", ActivityBusinessType.values());
    }

}
