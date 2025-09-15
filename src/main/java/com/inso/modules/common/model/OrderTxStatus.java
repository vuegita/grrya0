package com.inso.modules.common.model;

public enum OrderTxStatus {

    NEW("new", "待处理"),
    AUDIT("audit", "审核中"),
    PENDING("pending", "等待提交"),
    WAITING("waiting", "等待回调"),
    CAPTURED("captured", "回调成功"),
    REALIZED("realized", "处理完成"),
    REFUNDING("refunding", "处理中"),
    REFUND("refund", "退款完成"),
    FAILED("failed", "失败"),
    ;

    private String key;
    private String name;

    OrderTxStatus(String key, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

//    public String getName()
//    {
//        return name;
//    }

    public static OrderTxStatus getType(String key)
    {
        OrderTxStatus[] values = OrderTxStatus.values();
        for(OrderTxStatus type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

}
