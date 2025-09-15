package com.inso.modules.coin.core.cache;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;

public class CoinSettleConfigCacleKeyHelper {

    private static String ROOT_CACHE = CoinSettleConfigCacleKeyHelper.class.getName();


    public static String findByKey(String key, MyDimensionType dimensionType, CryptoNetworkType networkType)
    {
        return ROOT_CACHE + "findByKey_" + key + networkType.getKey() + dimensionType.getKey();
    }
}
