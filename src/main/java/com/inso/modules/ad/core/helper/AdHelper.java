package com.inso.modules.ad.core.helper;

import com.inso.modules.common.helper.IdGenerator;

public class AdHelper {

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    private static IdGenerator mIdGeneratorOfPurchase = IdGenerator.newSingleWorder();

    private static IdGenerator mTrackIdGenerator = IdGenerator.newSingleWorder();

    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId()
    {
        return mIdGenerator.nextId();
    }

    public static String nextOrderIdOfPurchase()
    {
        return mIdGeneratorOfPurchase.nextId();
    }

    public static String nextTrackId()
    {
        return mTrackIdGenerator.nextId(12);
    }

}
