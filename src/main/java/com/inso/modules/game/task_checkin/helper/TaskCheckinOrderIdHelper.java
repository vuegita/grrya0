package com.inso.modules.game.task_checkin.helper;

import com.inso.modules.common.helper.IdGenerator;

public class TaskCheckinOrderIdHelper {

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();


    /**
     * 生成订单号
     * @return
     */
    public static String nextOrderId()
    {
        return mIdGenerator.nextId(11);
    }


}
