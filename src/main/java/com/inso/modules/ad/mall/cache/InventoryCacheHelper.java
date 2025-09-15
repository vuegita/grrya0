package com.inso.modules.ad.mall.cache;

public class InventoryCacheHelper {

    private static final String ROOT_CACHE = InventoryCacheHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }


}
