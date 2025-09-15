package com.inso.modules.paychannel.model;

public enum ChannelStatus {
    ENABLE("enable", "启用", false),
    DISABLE("disable", "禁用", false),
    TEST("test", "测试", false),
    HIDDEN("hidden", "隐藏", true),
    ;

    private String key;
    private String title;
    private boolean needSupperAdmin = false;

    ChannelStatus(String key, String title, boolean needSupperAdmin)
    {
        this.key = key;
        this.title = title;
        this.needSupperAdmin = needSupperAdmin;
    }

    public String getKey()
    {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAdminPermit() {
        return needSupperAdmin;
    }

    public static ChannelStatus getType(String key)
    {
        ChannelStatus[] values = ChannelStatus.values();
        for(ChannelStatus type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
