package com.inso.modules.coin.core.cache;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;

public class ContractCacheKeyHelper {

    private static String ROOT_CACHE = ContractCacheKeyHelper.class.getName();



    public static String findById(long id)
    {
        return ROOT_CACHE + "findById_" + id;
    }

    public static String findByNetowrkAndCurrency(CryptoNetworkType networkType, CryptoCurrency currency)
    {
        return ROOT_CACHE + "findByNetowrkAndCurrency" + networkType.getKey() + currency.getKey();
    }


    public static String queryByNetwork(CryptoNetworkType networkType)
    {
        return ROOT_CACHE + "queryByNetwork_" + networkType.getKey();
    }


}
