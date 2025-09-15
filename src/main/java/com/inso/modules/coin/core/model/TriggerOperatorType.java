package com.inso.modules.coin.core.model;

public enum TriggerOperatorType {

    Admin("Admin", "Admin"),
    Agent("Agent", "Agent"),
    Member("Member", "Member"),
    Monitor("Monitor", "Monitor"),
    MONITOR_PREFIX("monitor_prefix", "monitor_prefix"),

    ;

    private String key;
    private String name;

    TriggerOperatorType(String key, String name)
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

    public static TriggerOperatorType getType(String key)
    {
        TriggerOperatorType[] values = TriggerOperatorType.values();
        for(TriggerOperatorType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }



}
