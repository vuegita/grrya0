package com.inso.modules.coin.helper;

import com.inso.modules.coin.CoinBusinessType;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.game.andar_bahar.model.ABType;

public class CoinHelper {


    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId(CoinBusinessType businessType)
    {
        return mIdGenerator.nextId(businessType.getCode());
    }


}
