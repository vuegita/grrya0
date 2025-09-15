package com.inso.modules.game.red_package.model;

import com.inso.framework.utils.StringUtils;

/**
 * 红包创建主体
 */
public enum RedPCreatorType {
    SYS("sys", 1 ), // 系统-只能在后台创建
    AGENT("agent",2), // 代理-只能在后台添加
    STAFF("staff",3), // 代理-只能在后台添加
    MEMBER("member",4), // 会员-只能在后台添加
    ;
    private String key;
    private String code;
    RedPCreatorType(String key, int code)
    {
        this.key = key;
        this.code = code + StringUtils.getEmpty();
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public static RedPCreatorType getType(String key)
    {
        RedPCreatorType[] values = RedPCreatorType.values();
        for(RedPCreatorType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
