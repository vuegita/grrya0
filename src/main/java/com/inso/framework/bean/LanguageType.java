package com.inso.framework.bean;

import com.inso.framework.utils.StringUtils;

public enum LanguageType {
    English("en"),
    Spanish("sp"), //西班牙语
    Hindi("hindy"),
    ;

    private String key;
    LanguageType(String key)
    {
      this.key=key;
    }

    public String getKey() {
        return key;
    }

    public static LanguageType getType(String key)
    {
        if(StringUtils.isEmpty(key))
        {
            return English;
        }
        LanguageType[] values = LanguageType.values();
        for(LanguageType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
