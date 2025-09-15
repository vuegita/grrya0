package com.inso.modules.paychannel.logical.payment.tajpay;

public enum PaymentTargetType {

    Topay("Topay", "https://www.topay.one"),
    Minepay("Minepay", "https://www.minepay.in"),
    Tajpay("Tajpay", "https://www.indiapay.io"),
    Payhub("Payhub", "https://www.payhub.cc"),
    Likepay("Likepay", "https://www.likepay.in"),
    Copay("Copay", "https://www.copay.cc"),

    ;

    private String key;
    private String server;

    PaymentTargetType(String key, String server)
    {
        this.key = key;
        this.server = server;
    }

    public String getKey() {
        return key;
    }

    public String getServer() {
        return server;
    }

    public static PaymentTargetType getType(String key)
    {
        PaymentTargetType[] values = PaymentTargetType.values();
        for(PaymentTargetType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}
