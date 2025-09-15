package com.inso.modules.report.model;

public enum StatsDimensionType {

    PLATFORM("platform"),
    AGENT("agent"),
    STAFF("staff"),
    ;

    private String key;
    private String name;

    StatsDimensionType(String key)
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

    public static StatsDimensionType getType(String key)
    {
        StatsDimensionType[] values = StatsDimensionType.values();
        for(StatsDimensionType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

}
