package com.inso.modules.coin.binance_activity.cache;

public class BARecordCacleKeyHelper {

    private static String ROOT_CACHE = BARecordCacleKeyHelper.class.getName();

//    public static String findById(long id)
//    {
//        return ROOT_CACHE + "findById" + id;
//    }
//
    public static String findByUserIdAndContractId(long userid, long contractid)
    {
        return ROOT_CACHE + "findByUserIdAndContractId" + userid + contractid;
    }



    public static String queryByUser(long userid)
    {
        return ROOT_CACHE + "queryByUser" + userid;
    }

    public static String queryByUserwallet(String username)
    {
        return ROOT_CACHE + "queryByUserwallet" + username;
    }


}
