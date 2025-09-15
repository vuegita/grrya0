package com.inso.modules.coin.defi_mining.cache;

public class MiningRecordCacleKeyHelper {

    private static String ROOT_CACHE = MiningRecordCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findByAccountIdAndProductId(long userid, long productid)
    {
        return ROOT_CACHE + "findByAccountIdAndProductId" + userid + productid;
    }



    public static String queryByUser(long userid)
    {
        return ROOT_CACHE + "queryByUser" + userid;
    }


}
