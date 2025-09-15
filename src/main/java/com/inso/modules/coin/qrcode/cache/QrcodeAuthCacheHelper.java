package com.inso.modules.coin.qrcode.cache;

public class QrcodeAuthCacheHelper {

    private static final String ROOT_CACHE = QrcodeAuthCacheHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }


}
