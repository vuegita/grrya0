package com.inso.modules.coin.core.cache;

import com.inso.modules.coin.core.model.CryptoNetworkType;

public class CoinAccountCacleKeyHelper {

    private static String ROOT_CACHE = CoinAccountCacleKeyHelper.class.getName();


    public static String findByAddress(String address, CryptoNetworkType networkType)
    {
        return ROOT_CACHE + "findByAddress_" + address + networkType.getKey();
    }

    public static String findByAddress2(String address)
    {
        return ROOT_CACHE + "findByAddress2" + address;
    }

    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String findByUserId(long userid)
    {
        return ROOT_CACHE + "findByUserId" + userid;
    }

    public static String findByAccountId(long accountId)
    {
        return ROOT_CACHE + "findByAccountId" + accountId;
    }



}
