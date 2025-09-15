package com.inso.modules.coin.core.cache;

import com.inso.modules.common.model.CryptoCurrency;

public class MutisignCacleKeyHelper {

    private static String ROOT_CACHE = MutisignCacleKeyHelper.class.getName();

    public static String findByid(long id)
    {
        return ROOT_CACHE + "findByid" + id;
    }

    public static String findByAddress( String address, CryptoCurrency currency)
    {
        return ROOT_CACHE + "findByAddress" + currency.getKey() + address ;
    }


}
