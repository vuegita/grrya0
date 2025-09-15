package com.inso.modules.game.lottery_game_impl.btc_kline.helper;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.helper.BetFeemoneyHelper;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BTCKlineHelper {

//    private static BigDecimal WIN_MONEY_MULTIPLE_2 = BetFeemoneyHelper.DEF_WIN_AMOUNT_2_RATE;
//    private static BigDecimal WIN_MONEY_MULTIPLE_NUMBER = BetFeemoneyHelper.DEF_WIN_AMOUNT_10_RATE;


    public static BigDecimal calcWinMoney(BigDecimal betAmount, String openItemType, String betItemType, boolean deductFee)
    {
        if(StringUtils.isEmpty(openItemType))
        {
            return BigDecimal.ZERO;
        }
        int rsOpenResult = StringUtils.asInt(openItemType);

//        BigDecimal singleFeemoney = BetFeemoneyHelper.getSingleFeemoney(betAmount);
        BigDecimal rsAmount = betAmount;

        BTCKlineBetItemType itemType = BTCKlineBetItemType.getType(betItemType);
        if(itemType == null)
        {
            int userBetResult = StringUtils.asInt(betItemType);
            if(userBetResult == rsOpenResult)
            {
                if(userBetResult == 0 || userBetResult == 1 || userBetResult == 9 ){
                    return rsAmount.multiply(BetFeemoneyHelper.getWinAmountMultiple_3(deductFee)).setScale(2, RoundingMode.HALF_UP);
                }else{
                    return rsAmount.multiply(BetFeemoneyHelper.getWinAmountMultiple_10(deductFee)).setScale(2, RoundingMode.HALF_UP);
                }

            }
            return BigDecimal.ZERO;
        }

        if(itemType == BTCKlineBetItemType.Big)
        {
            if(rsOpenResult >= 5)
            {
                return rsAmount.multiply(BetFeemoneyHelper.getWinAmountMultiple_2(deductFee)).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }
        if(itemType == BTCKlineBetItemType.Small)
        {
            if(rsOpenResult <= 4)
            {
                return rsAmount.multiply(BetFeemoneyHelper.getWinAmountMultiple_2(deductFee)).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        if(itemType == BTCKlineBetItemType.Odd)
        {
            if(rsOpenResult % 2 != 0 )
            {
                return rsAmount.multiply(BetFeemoneyHelper.getWinAmountMultiple_2(deductFee)).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        if(itemType == BTCKlineBetItemType.Even)
        {
            if(rsOpenResult % 2 == 0 )
            {
                return rsAmount.multiply(BetFeemoneyHelper.getWinAmountMultiple_2(deductFee)).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

//    /**
//     * 判断是否中奖
//     * @param openResult
//     * @param betItem
//     * @return
//     */
//    public static boolean isWin(String openResult, String betItem)
//    {
//        TurntableBetItemType openItemType = TurntableBetItemType.getType(openResult);
//        TurntableBetItemType betItemType = TurntableBetItemType.getType(betItem);
//        return openItemType == betItemType;
//    }

//    public static long getOpenResult(long openResult)
//    {
//        if(openResult >= 0 && openResult <= TurntableBetItemType.BET_ITEM_MAX_NUMBER)
//        {
//            return openResult;
//        }
//        return -1;
//    }

    public static void main(String[] args)
    {
        BigDecimal amount = new BigDecimal(1);
        String openResult = "5";
        String betItem = "Big";

        BigDecimal value = calcWinMoney(amount, openResult, betItem, true);

        System.out.println(value);

    }

}


