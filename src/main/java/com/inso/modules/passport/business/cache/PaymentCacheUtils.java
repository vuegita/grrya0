package com.inso.modules.passport.business.cache;

import com.inso.modules.passport.MyConstants;

public class PaymentCacheUtils {

    private static final String ROOT_CACHE = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_payment_";


    public static String payinCacheKey(String orderid)
    {
        return ROOT_CACHE  + "payin" + orderid;
    }

    public static String payoutCacheKey(String orderid)
    {
        return ROOT_CACHE + "payout" + orderid;
    }
}
