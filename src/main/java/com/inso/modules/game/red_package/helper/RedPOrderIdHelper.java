package com.inso.modules.game.red_package.helper;

import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.GameChildType;

public class RedPOrderIdHelper {

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    private static IdGenerator mPeriodIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @param abType
     * @return
     */
    public static String nextOrderId(GameChildType abType)
    {
        return mIdGenerator.nextId(abType.getCode());
    }

    /**
     * 期号订单号
     * @return
     */
    public static String nextPeriodOrderId()
    {
        return mPeriodIdGenerator.nextId(99);
    }
}
