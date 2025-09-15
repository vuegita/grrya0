package com.inso.modules.paychannel.cache;

import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;

public class ChannelCacheUtils {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PAY_CHANNEL_MODULE_NAME;

    public static String createFindChannelCache(long channelid)
    {
        return ROOT_CACHE_KEY + "createFindChannelCache" + channelid;
    }

    public static String createFindChannelWalletaddressCache(String walletaddress)
    {
        return ROOT_CACHE_KEY + "createFindChannelWalletaddressCache" + walletaddress;
    }

    public static String createQueryChannelListCache(ChannelType type, PayProductType productType, ICurrencyType currencyType)
    {
        if(productType != null)
        {
            String rs = ROOT_CACHE_KEY + "createQueryChannelListCache" + type.getKey() + productType.getKey();
            if(type == ChannelType.PAYOUT && productType == PayProductType.TAJPAY)
            {
                return rs + currencyType.getKey();
            }
            return rs;
        }
        else
        {
            return ROOT_CACHE_KEY + "createQueryChannelListCache" + type.getKey();
        }
    }
}
