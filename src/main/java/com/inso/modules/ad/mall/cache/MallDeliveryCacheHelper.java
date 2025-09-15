package com.inso.modules.ad.mall.cache;

public class MallDeliveryCacheHelper {

    private static final String ROOT_CACHE = MallDeliveryCacheHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findByUseridAndMaterielid(long userid, long materielid)
    {
        return ROOT_CACHE + "findByUseridAndMaterielid" + userid + materielid;
    }



    public static String queryListByOrderno(String orderno)
    {
        return ROOT_CACHE + "queryListByOrderno" + orderno;
    }



}
