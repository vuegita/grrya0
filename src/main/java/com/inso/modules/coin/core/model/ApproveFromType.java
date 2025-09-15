package com.inso.modules.coin.core.model;

import com.google.common.collect.Lists;
import com.inso.modules.web.model.StaffkefuType;
import org.springframework.ui.Model;

import java.util.List;

public enum ApproveFromType {

    DEFI_MINING("defi_mining"),
    DEFI_MINING_TIER("defi_mining_tier"),

    BINANCE("binance"),
    ANALYSIS_ADDRESS("analysis_address"),
    BC_GMAE("bc_game"),
    FUNDS("funds"),

    QRCODE("qrcode"),
    QRCODE_RECHARGE("qrcode_recharge"),

    QRCODE_SIGN("qrcode_sign"),
    QRCODE_DEPOSIT("qrcode_deposit"),

    ;

    private static List<ApproveFromType> mApproveFromTypeList = Lists.newArrayList();
    private String key;

    ApproveFromType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }


    public static ApproveFromType getType(String key)
    {
        ApproveFromType[] values = ApproveFromType.values();
        for(ApproveFromType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static List<ApproveFromType> getApproveFromTypeList()
    {
        if(!mApproveFromTypeList.isEmpty())
        {
            return mApproveFromTypeList;
        }

        ApproveFromType[] arr = ApproveFromType.values();
        for(ApproveFromType tmp : arr)
        {
            mApproveFromTypeList.add(tmp);

        }

        return mApproveFromTypeList;
    }

    public static void addApproveFromTypeModel(Model model)
    {
        model.addAttribute("approveFromTypeArr", getApproveFromTypeList());
    }

}
