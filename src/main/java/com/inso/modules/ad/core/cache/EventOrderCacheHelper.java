package com.inso.modules.ad.core.cache;

import com.inso.modules.common.model.OrderTxStatus;

public class EventOrderCacheHelper {

    public static final String ROOT_CACHE = EventOrderCacheHelper.class.getName();


    public static String queryLatestByUser(long userid)
    {
        return ROOT_CACHE + "_queryLatestByUser_" + userid;
    }

    public static String statsHistoryAll(long userid)
    {
        return ROOT_CACHE + "statsHistoryAll" + userid;
    }

    public static String findLatestOrderInfoByUserAndMaterielid(long userid, long materielid)
    {
        return ROOT_CACHE + "findLatestOrderInfoByUserAndMaterielid" + userid + materielid;
    }

    public static String queryLatestMaterielIds(long userid)
    {
        return ROOT_CACHE + "queryLatestMaterielIds" + userid;
    }

    public static String queryByUserAndTxStatus(long userid, OrderTxStatus status)
    {
        return ROOT_CACHE + "queryByUserAndTxStatus" + userid + status.getKey();
    }


    public static void main(String[] args) {
        String rs = "a" + 1 + 1;

        System.out.println(rs);
    }

}
