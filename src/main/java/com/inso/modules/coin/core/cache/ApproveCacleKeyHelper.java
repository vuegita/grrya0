package com.inso.modules.coin.core.cache;

import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.common.model.CryptoCurrency;

public class ApproveCacleKeyHelper {

    private static String ROOT_CACHE = ApproveCacleKeyHelper.class.getName();

    public static String findByid(long id)
    {
        return ROOT_CACHE + "findByid" + id;
    }

    public static String findByUnique(CryptoCurrency currency, String address, CryptoChainType chainType)
    {
        return ROOT_CACHE + "findByUnique_" + currency.getKey() + address + chainType.getKey();
    }

    public static String findByAccountAndContractId(long userid, long contractid)
    {
        return ROOT_CACHE + "findByAccountAndContractId"+ userid + contractid;
    }


}
