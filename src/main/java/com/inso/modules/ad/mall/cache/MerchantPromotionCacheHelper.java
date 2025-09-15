package com.inso.modules.ad.mall.cache;

public class MerchantPromotionCacheHelper {

    private static final String ROOT_CACHE = MerchantPromotionCacheHelper.class.getName();


    public static String findByUserId(long id)
    {
        return ROOT_CACHE + "findByUserId" + id;
    }


}
