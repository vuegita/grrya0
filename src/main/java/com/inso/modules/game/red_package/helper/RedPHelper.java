package com.inso.modules.game.red_package.helper;

import java.math.BigDecimal;

public class RedPHelper {

    /*** 中奖赔率 ***/
    private static final BigDecimal WIN_RATE = new BigDecimal(2); // 1:2


//    public static BigDecimal getSingleFeemoney(BigDecimal basicAmount)
//    {
//        return BetFeemoneyHelper.getSingleFeemoney(basicAmount);
//    }
//
//    public static BigDecimal getTotalFeemoney(BigDecimal basicAmount, long betCount)
//    {
//        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);
//        return singleFeemoney.multiply(new BigDecimal(betCount));
//    }
//
//    public static BigDecimal calcWinMoney(BigDecimal basicAmount, long betCountValue, RedPBetItemType openResult, RedPBetItemType betItem)
//    {
//        BigDecimal singleFeemoney = getSingleFeemoney(basicAmount);
//
//        if(openResult == betItem)
//        {
//            BigDecimal rsAmount = basicAmount.subtract(singleFeemoney);
//            BigDecimal betCount = new BigDecimal(betCountValue);
//            return betCount.multiply(rsAmount);
//        }
//        return BigDecimal.ZERO;
//    }

}
