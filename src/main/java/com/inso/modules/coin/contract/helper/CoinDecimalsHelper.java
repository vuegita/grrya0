package com.inso.modules.coin.contract.helper;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;

public class CoinDecimalsHelper {

    public static final String ROOT_CACHE = CoinDecimalsHelper.class.getName() + "_decimals_";

    public static void setValue(CryptoNetworkType networkType, String cryptoCurrencyAddr, int decimals)
    {
        String cachekey = ROOT_CACHE + networkType.getKey() + cryptoCurrencyAddr;
        CacheManager.getInstance().setString(cachekey, decimals + StringUtils.getEmpty());
    }

    public static int getValue(CryptoNetworkType networkType, String cryptoCurrencyAddr)
    {
        String cachekey = ROOT_CACHE  + networkType.getKey() + cryptoCurrencyAddr;
        String value = CacheManager.getInstance().getString(cachekey);

        if(StringUtils.isEmpty(value))
        {
            return -1;
        }
        return StringUtils.asInt(value);
    }



}
