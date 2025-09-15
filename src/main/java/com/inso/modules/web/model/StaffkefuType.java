package com.inso.modules.web.model;

import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import org.springframework.ui.Model;

import java.util.List;

public enum StaffkefuType {


    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    LINE("Line"),
    FACEBOOK("Facebook"),
    TWITTER("Twitter"),
    INSTAGRAM("Instagram"),
    SKYPE("Skype"),
    MESSENGER("Messenger"),
    CHATWOOT("chatwoot"),
    TAWK("tawk"),
    ;
    private static List<StaffkefuType> mStaffkefuTypeList = Lists.newArrayList();
    private String key;

    StaffkefuType(String key)
    {
        this.key = key;

    }

    public String getKey() {
        return key;
    }


    public static StaffkefuType getType(String key)
    {
        StaffkefuType[] values = StaffkefuType.values();
        for(StaffkefuType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public static List<StaffkefuType> getStaffkefuTypeList()
    {
        if(!mStaffkefuTypeList.isEmpty())
        {
            return mStaffkefuTypeList;
        }

        StaffkefuType[] arr = StaffkefuType.values();
        for(StaffkefuType tmp : arr)
        {
                mStaffkefuTypeList.add(tmp);

        }

        return mStaffkefuTypeList;
    }

    public static void addFreemarkerModel(Model model)
    {
        model.addAttribute("staffkefuTypeArr", getStaffkefuTypeList());
    }

    }
