package com.inso.modules.game.fm.helper;

import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.fm.model.FMType;

public class FMOrderIdHelper {

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @param abType
     * @return
     */
    public static String nextOrderId(FMType abType)
    {
        return mIdGenerator.nextId(abType.getCode());
    }
}
