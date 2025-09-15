package com.inso.modules.coin.withdraw.cache;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;

public class WithdralChannelCacheKeyHelper {

    private static String ROOT_CACHE = WithdralChannelCacheKeyHelper.class.getName();


    public static String findByKey(String key, MyDimensionType dimensionType, CryptoNetworkType networkType)
    {
        return ROOT_CACHE + "findByKey" + key + dimensionType.getKey() + networkType.getKey();
    }

}
