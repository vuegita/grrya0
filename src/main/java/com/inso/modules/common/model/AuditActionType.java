package com.inso.modules.common.model;

public enum AuditActionType {

    PASS("pass"),
    Refuse("refuse");

    private String key;

    private AuditActionType(String key)
    {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static AuditActionType getType(String key)
    {
        AuditActionType[] values = AuditActionType.values();
        for(AuditActionType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}
