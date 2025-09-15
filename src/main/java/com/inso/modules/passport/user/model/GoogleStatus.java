package com.inso.modules.passport.user.model;

public enum GoogleStatus {

    BIND("bind"),
    UNBIND("unbind"),
    ;

    private String key;

    GoogleStatus(String key)
    {
        this.key = key;
    }


    public String getKey()
    {
        return key;
    }

    public static GoogleStatus getType(String key)
    {
        GoogleStatus[] values = GoogleStatus.values();
        for(GoogleStatus type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
