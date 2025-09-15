package com.inso.modules.coin.cloud_mining.model;

import com.inso.modules.coin.core.model.ProfitConfigInfo;
import org.springframework.ui.Model;

public enum CloudProductType {

    COIN_CLOUD_ACTIVE(ProfitConfigInfo.ProfitType.COIN_CLOUD_ACTIVE.getKey(), "灵活挖矿"),

    COIN_CLOUD_SOLID("Coin_Cloud_Solid", "质押挖矿"),
    ;

    private String key;
    /*** 是不是代币Token ***/
    private String remark;

    CloudProductType(String key, String remark)
    {
        this.key = key;
        this.remark = remark;
    }

    public String getKey()
    {
        return key;
    }

    public String getRemark() {
        return remark;
    }

    public static CloudProductType getType(String key)
    {
        CloudProductType[] values = CloudProductType.values();
        for(CloudProductType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public static void addModel(Model model)
    {
        CloudProductType[] arr = CloudProductType.values();
        model.addAttribute("productTypeArr", arr);
    }
}
