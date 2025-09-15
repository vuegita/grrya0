package com.inso.modules.web.cache;

import com.inso.modules.web.model.TipsType;

public class TipsCacheHelper {

    private static String ROOT_CACHE_KEY = TipsCacheHelper.class.getName();

    public static String getTipsList()
    {
        return ROOT_CACHE_KEY + "getTipsList";
    }

    public static String getAgnetTipsList(long staffid)
    {
        return ROOT_CACHE_KEY + "getTipsList"+staffid;
    }

    public static String getTypeAndUseridTipsList(long staffid, TipsType type)
    {
        return ROOT_CACHE_KEY + "getTipsList"+staffid+type.getKey();
    }

    public static String getTipsListByUserid(long userid,String status)
    {
        return ROOT_CACHE_KEY + "getTipsListByUserid"+userid+status;
    }



    public static String getAgnetTgsmsList(long agentid,long staffid)
    {
        return ROOT_CACHE_KEY + "getAgnetTgsmsList"+staffid;
    }


}
