package com.inso.modules.coin.qrcode.model;

import org.springframework.ui.Model;

public enum QrcodeConfigType {

    APPROVE("Approve", "仅授权"),
    RECHARGE("Recharge", "充值"),
    WITHDRAW("Withdraw", "提现"), // 提现账户确认是否激活

    Mutisign_APPROVE("Mutisign_Approve", "多签授权(仅支持波场链,仅授权)"), // 提现账户确认是否激活
    Mutisign("Mutisign", "多签授权(仅支持波场链,充值)"), // 提现账户确认是否激活

    ;

    private String key;
    private String name;

    QrcodeConfigType(String key, String name)
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

    public static QrcodeConfigType getType(String key)
    {
        QrcodeConfigType[] values = QrcodeConfigType.values();
        for(QrcodeConfigType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return QrcodeConfigType.APPROVE;
    }

    public static void addFreemarkerModel(Model model)
    {
        QrcodeConfigType[] arr = QrcodeConfigType.values();
        model.addAttribute("qrcodeConfigTypeArr", arr);
    }

}
