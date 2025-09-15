package com.inso.modules.coin.cloud_mining.cache;

import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.common.model.CryptoCurrency;
import org.joda.time.DateTime;

public class CloudRecordCacleKeyHelper {

    private static String ROOT_CACHE = CloudRecordCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findByAccountIdAndProductId1(long userid, CloudProductType productType, CryptoCurrency currencyType, long days)
    {
        if(productType == CloudProductType.COIN_CLOUD_ACTIVE)
        {
            days = 0;
        }
        return ROOT_CACHE + "findByAccountIdAndProductId1" + userid + productType.getKey() + currencyType.getKey() + days;
    }

    public static String queryByAccountIdAndProductId(long userid, CloudProductType productType, CryptoCurrency currencyType, long days)
    {
        if(productType == CloudProductType.COIN_CLOUD_ACTIVE)
        {
            days = 0;
        }
        return ROOT_CACHE + "findByAccountIdAndProductId" + userid + productType.getKey() + currencyType.getKey() + days;
    }

    public static String withdrawOfDay(String username)
    {
        DateTime dateTime = DateTime.now();
        int dayOfYear = dateTime.getDayOfYear();
        return ROOT_CACHE + "withdrawOfDay" + username + dayOfYear;
    }



//    public static String queryByUser(long userid)
//    {
//        return ROOT_CACHE + "queryByUser" + userid;
//    }


}
