package com.inso.modules.common.model;

import org.springframework.ui.Model;

/**
 * 资金账户类型
 */
public enum FundAccountType {

    // 现货
    Spot("Spot","Spot", "Spot", false, "现货"), // 现货|币币

//    Margin("Margin","Margin", "Margin", false, "杠杆"), //
//
//    // U本位合约=BTCUSDT | 币本位合约=BTCUSD 直接以美元作为基础币
//    Futures("Futures","Futures", "Futures", false, "合约"), //
//
//    // 其它
//    C2C("C2C","C2C", "C2C", false,"C2C"), // C2C=场外交易
//    Earn("Earn","Earn", "Earn", false,"理财"), // 理财
//
//    Pool("Pool","Pool", "Pool", false,"矿池"), // 矿池
    ;

    private String category;
    private String key;
    private String name;
    private String remark;

    /*** 仅创建USDT账户 ***/
    private boolean onlySupportUSDT;

    private FundAccountType(String category, String key, String name, boolean onlySupportUSDT, String remark)
    {
        this.category = category;
        this.key = key;
        this.name = name;
        this.onlySupportUSDT = onlySupportUSDT;
        this.remark = remark;
    }

//    public String getCategory() {
//        return category;
//    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isOnlySupportUSDT() {
        return onlySupportUSDT;
    }

    public static FundAccountType getType(String key)
    {
        FundAccountType[] values = FundAccountType.values();
        for(FundAccountType type : values)
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
        FundAccountType[] fundAccountTypeArr = FundAccountType.values();
        model.addAttribute("fundAccountTypeArr", fundAccountTypeArr);
    }


}

