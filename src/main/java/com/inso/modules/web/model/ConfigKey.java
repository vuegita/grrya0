package com.inso.modules.web.model;

public class ConfigKey  {

    private String key;
    private String value;
//    private String remark;

    public static String getColumnPrefix(){
        return "config";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

//    public String getRemark() {
//        return remark;
//    }

//    public void setRemark(String remark) {
//        this.remark = remark;
//    }
}
