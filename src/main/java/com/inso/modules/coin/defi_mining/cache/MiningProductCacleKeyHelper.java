package com.inso.modules.coin.defi_mining.cache;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;

public class MiningProductCacleKeyHelper {

    private static String ROOT_CACHE = MiningProductCacleKeyHelper.class.getName();


    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findByCurrencyAndNetwork(CryptoCurrency baseCurrency, CryptoNetworkType networkType)
    {
        return ROOT_CACHE + "findByCurrencyAndNetwork" + baseCurrency.getKey() + networkType.getKey();
    }

    public static String queryAllList()
    {
        return ROOT_CACHE + "queryAllList";
    }


}
